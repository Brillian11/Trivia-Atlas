package com.example.data.remote

import android.util.Log
import com.example.data.local.CachedQuestionEntity
import com.example.data.local.OfflineTriviaSeed
import com.example.data.model.QuizDifficulty
import com.example.data.model.QuizMode
import com.example.data.model.TriviaCategory
import com.example.data.model.TriviaQuestion
import com.example.data.model.WikipediaLanguageMapper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Zero-key online trivia, translation, encyclopedic explanation, and visual Wikipedia helpers.
 * Ensures live English, Spanish, Indonesian, and Picture Atlas quizzes always pull fresh online data
 * without requiring paid API subscriptions or Firebase AI billing.
 */
class OnlineTriviaSources {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(14, TimeUnit.SECONDS)
        .build()

    suspend fun fetchOpenTdbQuestions(
        category: TriviaCategory,
        difficulty: QuizDifficulty,
        amount: Int,
        explicitCategoryId: Int? = null
    ): List<CachedQuestionEntity> = withContext(Dispatchers.IO) {
        try {
            val safeAmount = amount.coerceIn(5, 30)
            val catId = explicitCategoryId ?: openTdbCategoryId(category)
            val url = buildString {
                append("https://opentdb.com/api.php?amount=").append(safeAmount)
                append("&type=multiple")
                if (catId != null) append("&category=").append(catId)
                if (difficulty != QuizDifficulty.ALL) append("&difficulty=").append(difficulty.apiKey)
            }
            val body = executeGet(url) ?: return@withContext emptyList()
            val root = JSONObject(body)
            if (root.optInt("response_code", -1) != 0) return@withContext emptyList()
            val results = root.optJSONArray("results") ?: return@withContext emptyList()
            val now = System.currentTimeMillis()
            val list = ArrayList<CachedQuestionEntity>(results.length())

            for (i in 0 until results.length()) {
                val item = results.optJSONObject(i) ?: continue
                val qText = decodeHtml(item.optString("question")).trim()
                val correct = decodeHtml(item.optString("correct_answer")).trim()
                val rawInc = item.optJSONArray("incorrect_answers") ?: continue
                val incorrects = (0 until rawInc.length())
                    .map { decodeHtml(rawInc.optString(it)).trim() }
                    .filter { it.isNotBlank() && !it.equals(correct, ignoreCase = true) }
                    .distinct()
                if (qText.isBlank() || correct.isBlank() || incorrects.size < 2) continue

                val resolvedCategory = if (category == TriviaCategory.MIXED) {
                    mapOpenTdbCategoryName(decodeHtml(item.optString("category")))
                } else {
                    category
                }
                val resolvedDiff = QuizDifficulty.fromKey(item.optString("difficulty", "medium")).let {
                    if (it == QuizDifficulty.ALL) QuizDifficulty.MEDIUM else it
                }
                val idSeed = (qText + correct).hashCode().toUInt().toString(16)
                list.add(
                    CachedQuestionEntity(
                        id = "opentdb_$idSeed",
                        category = resolvedCategory.apiKey,
                        difficulty = resolvedDiff.apiKey,
                        questionText = qText,
                        correctAnswer = correct,
                        incorrectAnswersSerialized = OfflineTriviaSeed.encodeIncorrect(incorrects),
                        pictureQuestion = false,
                        explanation = "",
                        cachedAt = now
                    )
                )
            }
            list
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            runCatching { Log.w(TAG, "OpenTDB fetch failed", e) }
            emptyList()
        }
    }

    /**
     * Fetches real, illustrated visual trivia questions online directly from Wikipedia
     * in the requested language (en / es / id). Never reads local seeds.
     */
    suspend fun fetchLiveWikipediaPictureQuestions(
        languageCode: String,
        mode: QuizMode,
        count: Int,
        excludeIds: Set<String>,
        rng: Random
    ): List<TriviaQuestion> = withContext(Dispatchers.IO) {
        val wikiLang = WikipediaLanguageMapper.toWikipediaSubdomain(languageCode)
        val normLang = when (languageCode.lowercase()) {
            "es" -> "es"
            "in", "id" -> "in"
            else -> "en"
        }

        val topicPools = visualTopicPoolsForLang(wikiLang).shuffled(rng)
        val collected = ArrayList<TriviaQuestion>(count)
        val usedIds = excludeIds.toMutableSet()
        val usedAnswers = HashSet<String>()

        // Pull from 3 randomized topic clusters + 1 random fallback cluster so each session varies broadly
        for (poolTopic in topicPools.take(4)) {
            if (collected.size >= count) break
            try {
                val pages = queryIllustratedWikiPages(
                    wikiLang = wikiLang,
                    searchQuery = poolTopic.query,
                    offset = rng.nextInt(0, 28),
                    limit = 22
                )
                val validCandidates = pages.filter { page ->
                    val cleanTitle = cleanArticleTitle(page.title)
                    val id = "wiki_pic_${wikiLang}_${page.pageId}"
                    page.pageId > 0L &&
                        id !in usedIds &&
                        cleanTitle.length in 3..42 &&
                        !cleanTitle.any { it == ':' || it == '/' } &&
                        !isDisambiguationOrListTitle(cleanTitle) &&
                        !page.thumbnailUrl.isNullOrBlank() &&
                        page.extract.length >= 45 &&
                        (mode != QuizMode.GUESS || cleanTitle.split(" ").size <= 4)
                }

                if (validCandidates.size < 4) continue
                val peerTitles = validCandidates
                    .map { cleanArticleTitle(it.title) }
                    .distinctBy { it.lowercase() }

                for (candidate in validCandidates.shuffled(rng)) {
                    if (collected.size >= count) break
                    val answer = cleanArticleTitle(candidate.title)
                    if (!usedAnswers.add(answer.lowercase())) continue
                    val distractors = peerTitles
                        .filter { !it.equals(answer, ignoreCase = true) }
                        .shuffled(rng)
                        .take(3)
                    if (distractors.size < 3) continue

                    val clueText = buildVisualClueText(
                        answer = answer,
                        description = candidate.description,
                        extract = candidate.extract,
                        wikiLang = wikiLang,
                        normLang = normLang
                    ) ?: continue

                    val questionId = "wiki_pic_${wikiLang}_${candidate.pageId}"
                    usedIds.add(questionId)

                    val explanation = trimToSentences(candidate.extract, maxSentences = 2, maxChars = 280)
                        .ifBlank { clueText }

                    collected.add(
                        TriviaQuestion(
                            id = questionId,
                            category = poolTopic.category,
                            difficulty = poolTopic.difficulty,
                            questionText = clueText,
                            correctAnswer = answer,
                            allOptions = (distractors + answer).distinct().shuffled(rng),
                            pictureQuestion = true,
                            imageDrawableRes = null,
                            imageUrl = candidate.thumbnailUrl,
                            illustrationTag = null,
                            explanation = explanation
                        )
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                runCatching { Log.w(TAG, "Wikipedia visual topic fetch failed for ${poolTopic.query}", e) }
            }
        }

        // Top up from random illustrated mainspace articles if still short
        if (collected.size < count) {
            try {
                val randomPages = queryRandomIllustratedWikiPages(wikiLang, limit = 30)
                val validRandom = randomPages.filter { page ->
                    val cleanTitle = cleanArticleTitle(page.title)
                    val id = "wiki_pic_${wikiLang}_${page.pageId}"
                    page.pageId > 0L &&
                        id !in usedIds &&
                        cleanTitle.length in 3..42 &&
                        !cleanTitle.any { it == ':' || it == '/' } &&
                        !isDisambiguationOrListTitle(cleanTitle) &&
                        !page.thumbnailUrl.isNullOrBlank() &&
                        page.extract.length >= 45 &&
                        (mode != QuizMode.GUESS || cleanTitle.split(" ").size <= 4)
                }
                val peerTitles = validRandom
                    .map { cleanArticleTitle(it.title) }
                    .distinctBy { it.lowercase() }

                for (candidate in validRandom.shuffled(rng)) {
                    if (collected.size >= count) break
                    val answer = cleanArticleTitle(candidate.title)
                    if (!usedAnswers.add(answer.lowercase())) continue
                    val distractors = peerTitles
                        .filter { !it.equals(answer, ignoreCase = true) }
                        .shuffled(rng)
                        .take(3)
                    if (distractors.size < 3) continue
                    val clueText = buildVisualClueText(
                        answer = answer,
                        description = candidate.description,
                        extract = candidate.extract,
                        wikiLang = wikiLang,
                        normLang = normLang
                    ) ?: continue
                    val questionId = "wiki_pic_${wikiLang}_${candidate.pageId}"
                    usedIds.add(questionId)
                    collected.add(
                        TriviaQuestion(
                            id = questionId,
                            category = TriviaCategory.PICTURE_ATLAS,
                            difficulty = QuizDifficulty.MEDIUM,
                            questionText = clueText,
                            correctAnswer = answer,
                            allOptions = (distractors + answer).distinct().shuffled(rng),
                            pictureQuestion = true,
                            imageDrawableRes = null,
                            imageUrl = candidate.thumbnailUrl,
                            illustrationTag = null,
                            explanation = trimToSentences(candidate.extract, 2, 280)
                        )
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                runCatching { Log.w(TAG, "Wikipedia random picture fallback failed", e) }
            }
        }

        collected.shuffled(rng).take(count)
    }

    /**
     * Translates a list of English TriviaQuestion objects cleanly into Spanish (es) or Indonesian (in/id)
     * via the public Google Translate GTX endpoint (falling back to MyMemory per-item if needed).
     * Returns only questions whose translations succeeded cleanly and kept distinct options.
     */
    suspend fun translateQuestionsOnline(
        questions: List<TriviaQuestion>,
        targetLanguageCode: String,
        rng: Random
    ): List<TriviaQuestion> = withContext(Dispatchers.IO) {
        if (questions.isEmpty()) return@withContext emptyList()
        val tl = when (targetLanguageCode.lowercase()) {
            "es" -> "es"
            "in", "id" -> "id"
            else -> return@withContext questions
        }

        // Batch strings cleanly in small groups of 3 questions (~15 fields per request)
        val batches = questions.chunked(3)
        val translatedResults = coroutineScope {
            batches.map { batch ->
                async {
                    translateQuestionBatch(batch, tl, rng)
                }
            }.awaitAll().flatten()
        }
        translatedResults
    }

    private suspend fun translateQuestionBatch(
        batch: List<TriviaQuestion>,
        targetLang: String,
        rng: Random
    ): List<TriviaQuestion> {
        // Flatten fields: for each question we preserve [questionText, correctAnswer, option1, option2, option3]
        data class ItemMeta(
            val source: TriviaQuestion,
            val incorrectSources: List<String>,
            val startIndex: Int,
            val fieldCount: Int
        )

        val entries = ArrayList<String>()
        val metas = ArrayList<ItemMeta>(batch.size)
        for (q in batch) {
            val incorrects = q.allOptions.filter { it != q.correctAnswer }
            val start = entries.size
            entries.add(q.questionText)
            entries.add(q.correctAnswer)
            incorrects.forEach { entries.add(it) }
            metas.add(ItemMeta(q, incorrects, start, 2 + incorrects.size))
        }

        val translatedFields = translateLinesGtx(entries, targetLang)
            ?: return emptyList()

        if (translatedFields.size != entries.size) {
            return emptyList()
        }

        return metas.mapNotNull { meta ->
            val qTranslated = translatedFields.getOrNull(meta.startIndex)?.trim().orEmpty()
            val cTranslated = translatedFields.getOrNull(meta.startIndex + 1)?.trim().orEmpty()
            if (qTranslated.isBlank() || cTranslated.isBlank()) return@mapNotNull null

            val rawIncorrectTranslated = (0 until meta.incorrectSources.size).map { offset ->
                translatedFields.getOrNull(meta.startIndex + 2 + offset)?.trim()
                    ?.takeIf { it.isNotBlank() }
                    ?: meta.incorrectSources[offset]
            }

            // Ensure distractors stay distinct from the correct answer
            val finalIncorrect = rawIncorrectTranslated.mapIndexed { idx, candidate ->
                if (candidate.equals(cTranslated, ignoreCase = true)) {
                    meta.incorrectSources[idx]
                } else {
                    candidate
                }
            }.filter { !it.equals(cTranslated, ignoreCase = true) }.distinct()

            if (finalIncorrect.size < 2) return@mapNotNull null

            val normLang = if (targetLang == "id") "in" else targetLang
            meta.source.copy(
                questionText = qTranslated,
                correctAnswer = cTranslated,
                allOptions = (finalIncorrect + cTranslated).distinct().shuffled(rng),
                explanation = buildConciseFallbackExplanation(
                    answer = cTranslated,
                    category = meta.source.category,
                    languageCode = normLang
                )
            )
        }
    }

    private fun translateLinesGtx(lines: List<String>, targetLang: String): List<String>? {
        if (lines.isEmpty()) return emptyList()
        // Use a rare numbered tag marker that Google Translate preserves cleanly line-by-line
        val delimiter = "\n§§§\n"
        val joined = lines.joinToString(delimiter) { it.replace("\n", " ").trim() }
        val translatedJoined = requestGtxTranslation(joined, targetLang) ?: return null

        val split = translatedJoined.split(Regex("\\s*§§§\\s*"))
        if (split.size == lines.size) {
            return split.map { it.trim() }
        }

        // Fallback: translate one by one quickly if delimiter shifted
        val fallbackList = ArrayList<String>(lines.size)
        for (line in lines) {
            val single = requestGtxTranslation(line, targetLang) ?: return null
            fallbackList.add(single.trim())
        }
        return fallbackList
    }

    private fun requestGtxTranslation(text: String, targetLang: String): String? {
        return try {
            val encoded = URLEncoder.encode(text, "UTF-8")
            val url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=$targetLang&dt=t&q=$encoded"
            val body = executeGet(url) ?: return null
            val outer = JSONArray(body)
            val segments = outer.optJSONArray(0) ?: return null
            val sb = StringBuilder()
            for (i in 0 until segments.length()) {
                val seg = segments.optJSONArray(i) ?: continue
                sb.append(seg.optString(0, ""))
            }
            sb.toString().takeIf { it.isNotBlank() }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Batch-enriches explanations for trivia questions using Wikipedia summaries in the user's language.
     * Handles both named-entity answers ("What is [name]?") and conceptual / explanation questions
     * ("Why/How/What causes...?" with clause or phrase answers), plus cross-language EN fallback.
     */
    suspend fun enrichExplanationsWithWikipedia(
        questions: List<TriviaQuestion>,
        languageCode: String,
        englishFallbackById: Map<String, TriviaQuestion> = emptyMap()
    ): List<TriviaQuestion> = withContext(Dispatchers.IO) {
        if (questions.isEmpty()) return@withContext questions
        val wikiLang = WikipediaLanguageMapper.toWikipediaSubdomain(languageCode)
        val normLang = when (languageCode.lowercase()) {
            "es" -> "es"
            "in", "id" -> "in"
            else -> "en"
        }
        val translateLang = when (normLang) {
            "es" -> "es"
            "in" -> "id"
            else -> "en"
        }

        val needsEnrichment = questions.filter { isPlaceholderOrFallbackExplanation(it.explanation) }
        if (needsEnrichment.isEmpty()) return@withContext questions

        val directTitles = needsEnrichment
            .map { it.correctAnswer.trim() }
            .filter { isSearchableSubject(it) }
            .distinct()

        val explanationsByAnswer = withTimeoutOrNull(6500L) {
            fetchBatchWikiSummaries(
                titles = directTitles,
                wikiLang = wikiLang
            )
        }.orEmpty()

        // Also batch-fetch English titles in parallel when running localized quizzes for subjects missing local Wikipedia pages
        val enExplanationsByAnswer = if (translateLang != "en" && englishFallbackById.isNotEmpty()) {
            val missingDirectEn = needsEnrichment
                .filter { explanationsByAnswer[it.correctAnswer.trim().lowercase()].isNullOrBlank() }
                .mapNotNull { englishFallbackById[it.id]?.correctAnswer?.trim() }
                .filter { isSearchableSubject(it) }
                .distinct()
            if (missingDirectEn.isNotEmpty()) {
                withTimeoutOrNull(5500L) {
                    fetchBatchWikiSummaries(
                        titles = missingDirectEn,
                        wikiLang = "en"
                    )
                }.orEmpty()
            } else {
                emptyMap()
            }
        } else {
            emptyMap()
        }

        val remaining = needsEnrichment.filter { q ->
            val key = q.correctAnswer.trim().lowercase()
            explanationsByAnswer[key].isNullOrBlank()
        }

        val searchByQuestionId = if (remaining.isNotEmpty()) {
            withTimeoutOrNull(14000L) {
                coroutineScope {
                    remaining.map { q ->
                        async {
                            // Per-question timeout ensures one stalled lookup never cancels other resolved questions
                            val resolved = withTimeoutOrNull(7500L) {
                                resolveExplanationForQuestion(
                                    question = q,
                                    englishQuestion = englishFallbackById[q.id],
                                    wikiLang = wikiLang,
                                    translateLang = translateLang,
                                    enExplanationsByAnswer = enExplanationsByAnswer
                                )
                            }
                            q.id to resolved
                        }
                    }.awaitAll().toMap()
                }
            }.orEmpty()
        } else {
            emptyMap()
        }

        questions.map { q ->
            if (!isPlaceholderOrFallbackExplanation(q.explanation)) {
                return@map q
            }
            val key = q.correctAnswer.trim().lowercase()
            val wikiFact = explanationsByAnswer[key]?.takeIf { it.isNotBlank() }
                ?: searchByQuestionId[q.id]?.takeIf { !it.isNullOrBlank() }

            val finalExplanation = wikiFact ?: buildContextualFallbackExplanation(
                questionText = q.questionText,
                answer = q.correctAnswer,
                category = q.category,
                languageCode = normLang
            )
            q.copy(explanation = finalExplanation)
        }
    }

    private fun resolveExplanationForQuestion(
        question: TriviaQuestion,
        englishQuestion: TriviaQuestion?,
        wikiLang: String,
        translateLang: String,
        enExplanationsByAnswer: Map<String, String>
    ): String? {
        val localQueries = buildWikiSearchQueries(question.questionText, question.correctAnswer)
        val localRelevance = extractKeywords("${question.questionText} ${question.correctAnswer}")
            .map { it.lowercase() }
            .toSet()

        // Try up to 2 primary queries in target Wikipedia
        for (query in localQueries.take(2)) {
            val hit = searchWikiSummary(query, wikiLang, localRelevance)
            if (!hit.isNullOrBlank()) return hit
        }

        // Cross-language English Wikipedia fallback for ES / ID quizzes (much deeper concept coverage)
        if (translateLang != "en") {
            val enKey = englishQuestion?.correctAnswer?.trim()?.lowercase()
            val enBatchHit = enKey?.let { enExplanationsByAnswer[it] }?.takeIf { it.isNotBlank() }
            if (enBatchHit != null) {
                return requestGtxTranslation(enBatchHit, translateLang)?.trim()?.takeIf { it.isNotBlank() }
                    ?: enBatchHit
            }

            val enQ = englishQuestion ?: question
            val enQueries = buildWikiSearchQueries(enQ.questionText, enQ.correctAnswer)
            val enRelevance = extractKeywords("${enQ.questionText} ${enQ.correctAnswer}")
                .map { it.lowercase() }
                .toSet()

            for (query in enQueries.take(2)) {
                val enSummary = searchWikiSummary(query, "en", enRelevance)
                if (!enSummary.isNullOrBlank()) {
                    return requestGtxTranslation(enSummary, translateLang)?.trim()?.takeIf { it.isNotBlank() }
                        ?: enSummary
                }
            }
        }

        // Last local query (e.g., answer-only concept keywords) if not yet tried
        if (localQueries.size > 2) {
            val thirdHit = searchWikiSummary(localQueries[2], wikiLang, localRelevance)
            if (!thirdHit.isNullOrBlank()) return thirdHit
        }

        return null
    }

    private fun fetchBatchWikiSummaries(
        titles: List<String>,
        wikiLang: String
    ): Map<String, String> {
        if (titles.isEmpty()) return emptyMap()
        val result = HashMap<String, String>()
        titles.chunked(18).forEach { chunk ->
            try {
                val joinedTitles = chunk.joinToString("|") { it.trim().replace(" ", "_") }
                val url = "https://$wikiLang.wikipedia.org/w/api.php" +
                    "?action=query&titles=${URLEncoder.encode(joinedTitles, "UTF-8")}" +
                    "&prop=extracts&exintro=1&explaintext=1&exsentences=3" +
                    "&redirects=1&format=json&formatversion=2"
                val body = executeGet(url) ?: return@forEach
                val root = JSONObject(body)
                val query = root.optJSONObject("query") ?: return@forEach

                // Map normalized/redirected titles back to original requested keys
                val titleAliases = HashMap<String, MutableList<String>>()
                chunk.forEach { orig ->
                    titleAliases.getOrPut(orig.trim().lowercase()) { mutableListOf() }.add(orig.trim().lowercase())
                }
                val normalized = query.optJSONArray("normalized")
                if (normalized != null) {
                    for (i in 0 until normalized.length()) {
                        val obj = normalized.optJSONObject(i) ?: continue
                        val from = obj.optString("from").trim().lowercase()
                        val to = obj.optString("to").trim().lowercase()
                        titleAliases.getOrPut(to) { mutableListOf() }.add(from)
                    }
                }
                val redirects = query.optJSONArray("redirects")
                if (redirects != null) {
                    for (i in 0 until redirects.length()) {
                        val obj = redirects.optJSONObject(i) ?: continue
                        val from = obj.optString("from").trim().lowercase()
                        val to = obj.optString("to").trim().lowercase()
                        val bucket = titleAliases.getOrPut(to) { mutableListOf() }
                        bucket.add(from)
                        titleAliases[from]?.let { bucket.addAll(it) }
                    }
                }

                val pages = query.optJSONArray("pages") ?: return@forEach
                for (i in 0 until pages.length()) {
                    val page = pages.optJSONObject(i) ?: continue
                    if (page.optBoolean("missing", false)) continue
                    val title = page.optString("title").trim()
                    val extract = page.optString("extract").trim()
                    if (title.isBlank() || extract.length < 30 || isDisambiguationText(extract)) continue
                    val summary = trimToSentences(extract, maxSentences = 2, maxChars = 320)
                    val titleKey = title.lowercase()
                    result[titleKey] = summary
                    titleAliases[titleKey]?.forEach { alias -> result[alias] = summary }
                }
            } catch (_: Exception) {
                // Continue gracefully
            }
        }
        return result
    }

    private fun searchWikiSummary(
        term: String,
        wikiLang: String,
        relevanceTerms: Set<String> = emptySet()
    ): String? {
        return try {
            val url = "https://$wikiLang.wikipedia.org/w/api.php" +
                "?action=query&generator=search&gsrsearch=${URLEncoder.encode(term, "UTF-8")}" +
                "&gsrnamespace=0&gsrlimit=4&prop=extracts&exintro=1&explaintext=1&exsentences=3" +
                "&format=json&formatversion=2"
            val body = executeGet(url) ?: return null
            val pages = JSONObject(body).optJSONObject("query")?.optJSONArray("pages") ?: return null

            var bestExtract: String? = null
            var bestScore = -1
            for (i in 0 until pages.length()) {
                val page = pages.optJSONObject(i) ?: continue
                val title = page.optString("title").trim()
                val extract = page.optString("extract").trim()
                if (extract.length < 35 || isDisambiguationText(extract) || isDisambiguationOrListTitle(title)) continue

                val haystack = "${title.lowercase()} ${extract.lowercase()}"
                val overlapScore = if (relevanceTerms.isEmpty()) {
                    1
                } else {
                    relevanceTerms.count { token -> haystack.contains(token) }
                }
                val rankBonus = (4 - i).coerceAtLeast(0)
                val totalScore = overlapScore * 10 + rankBonus
                if (totalScore > bestScore) {
                    bestScore = totalScore
                    bestExtract = extract
                }
            }
            bestExtract?.let { trimToSentences(it, maxSentences = 2, maxChars = 320) }
        } catch (_: Exception) {
            null
        }
    }

    private data class WikiVisualCandidate(
        val pageId: Long,
        val title: String,
        val description: String?,
        val extract: String,
        val thumbnailUrl: String?
    )

    private fun queryIllustratedWikiPages(
        wikiLang: String,
        searchQuery: String,
        offset: Int,
        limit: Int
    ): List<WikiVisualCandidate> {
        val url = "https://$wikiLang.wikipedia.org/w/api.php" +
            "?action=query&generator=search&gsrsearch=${URLEncoder.encode(searchQuery, "UTF-8")}" +
            "&gsrnamespace=0&gsroffset=$offset&gsrlimit=$limit" +
            "&prop=extracts|pageimages|description&exintro=1&explaintext=1&exsentences=2" +
            "&piprop=thumbnail&pilicense=any&pithumbsize=900&pilimit=max" +
            "&redirects=1&format=json&formatversion=2"
        return parseVisualWikiResponse(executeGet(url))
    }

    private fun queryRandomIllustratedWikiPages(
        wikiLang: String,
        limit: Int
    ): List<WikiVisualCandidate> {
        val url = "https://$wikiLang.wikipedia.org/w/api.php" +
            "?action=query&generator=random&grnnamespace=0&grnlimit=$limit" +
            "&prop=extracts|pageimages|description&exintro=1&explaintext=1&exsentences=2" +
            "&piprop=thumbnail&pilicense=any&pithumbsize=900&pilimit=max" +
            "&redirects=1&format=json&formatversion=2"
        return parseVisualWikiResponse(executeGet(url))
    }

    private fun parseVisualWikiResponse(body: String?): List<WikiVisualCandidate> {
        if (body.isNullOrBlank()) return emptyList()
        val pages = JSONObject(body).optJSONObject("query")?.optJSONArray("pages") ?: return emptyList()
        val list = ArrayList<WikiVisualCandidate>(pages.length())
        for (i in 0 until pages.length()) {
            val page = pages.optJSONObject(i) ?: continue
            val pageId = page.optLong("pageid", 0L)
            val title = page.optString("title").trim()
            val desc = page.optString("description").trim().takeIf { it.isNotBlank() }
            val extract = page.optString("extract").trim()
            val thumbObj = page.optJSONObject("thumbnail")
            val thumbUrl = thumbObj?.optString("source")?.trim()?.takeIf { url ->
                url.startsWith("http") &&
                    !url.endsWith(".svg", ignoreCase = true) &&
                    !url.endsWith(".ogg", ignoreCase = true) &&
                    !url.endsWith(".webm", ignoreCase = true) &&
                    !url.contains("Flag_of_", ignoreCase = true)
            }
            val thumbWidth = thumbObj?.optInt("width", 0) ?: 0
            if (pageId > 0L && title.isNotBlank() && thumbUrl != null && thumbWidth >= 220 && !isDisambiguationText(extract)) {
                list.add(
                    WikiVisualCandidate(
                        pageId = pageId,
                        title = title,
                        description = desc,
                        extract = extract,
                        thumbnailUrl = thumbUrl
                    )
                )
            }
        }
        return list
    }

    private fun buildVisualClueText(
        answer: String,
        description: String?,
        extract: String,
        wikiLang: String,
        normLang: String
    ): String? {
        val rawClue = when {
            !description.isNullOrBlank() && description.length in 10..90 -> description
            extract.isNotBlank() -> trimToSentences(extract, maxSentences = 1, maxChars = 135)
            else -> return null
        }
        // Mask exact title or words of title so the visual question never gives away the answer in text
        var redacted = rawClue.replace(Regex(Regex.escape(answer), RegexOption.IGNORE_CASE), "_____")
        val longTokens = answer.split(Regex("\\s+")).filter { it.length >= 5 }
        for (token in longTokens) {
            redacted = redacted.replace(Regex("\\b${Regex.escape(token)}\\b", RegexOption.IGNORE_CASE), "_____")
        }
        redacted = redacted.trim().trimEnd('.')
        if (redacted.length < 8) return null

        return when (normLang) {
            "es" -> "Observa la imagen. ¿Qué elemento mostrado aquí corresponde a: \"$redacted\"?"
            "in" -> "Perhatikan gambar ini. Subjek manakah yang dijelaskan sebagai: \"$redacted\"?"
            else -> "Look at the image. Which subject shown here matches: \"$redacted\"?"
        }
    }

    private fun buildConciseFallbackExplanation(
        answer: String,
        category: TriviaCategory,
        languageCode: String
    ): String = when (languageCode.lowercase()) {
        "es" -> "La respuesta correcta es \"$answer\" en la categoría ${category.title}."
        "in", "id" -> "Jawaban yang benar adalah \"$answer\" pada kategori ${category.title}."
        else -> "The correct answer is \"$answer\" (${category.title})."
    }

    private fun cleanArticleTitle(title: String): String =
        title.replace(Regex("\\s*\\([^)]*\\)$"), "").trim()

    private fun isDisambiguationOrListTitle(title: String): Boolean {
        val lower = title.lowercase()
        return lower.startsWith("list of ") ||
            lower.startsWith("anexo:") ||
            lower.startsWith("daftar ") ||
            lower.contains("disambiguation") ||
            lower.contains("desambiguación") ||
            lower.all { it.isDigit() || it == '-' || it == '–' }
    }

    private fun isDisambiguationText(text: String): Boolean {
        val lower = text.lowercase()
        return lower.contains("may refer to:") ||
            lower.contains("puede referirse a:") ||
            lower.contains("dapat mengacu kepada:") ||
            lower.contains("dapat merujuk kepada:")
    }

    private fun trimToSentences(text: String, maxSentences: Int, maxChars: Int): String {
        val cleaned = text.replace(Regex("\\s+"), " ").trim()
        if (cleaned.isEmpty()) return ""
        val sentences = cleaned.split(Regex("(?<=[.!?])\\s+")).filter { it.isNotBlank() }
        val joined = sentences.take(maxSentences).joinToString(" ")
        return if (joined.length <= maxChars) joined else joined.take(maxChars - 1).trimEnd() + "…"
    }

    private fun executeGet(url: String): String? {
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", WIKI_USER_AGENT)
            .header("Cache-Control", "no-cache")
            .get()
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            return response.body?.string()
        }
    }

    private fun openTdbCategoryId(category: TriviaCategory): Int? = when (category) {
        TriviaCategory.GENERAL_KNOWLEDGE -> 9
        TriviaCategory.SCIENCE -> 17
        TriviaCategory.TECHNOLOGY -> if (Random.nextBoolean()) 18 else 30
        TriviaCategory.HISTORY -> 23
        TriviaCategory.GEOGRAPHY -> 22
        TriviaCategory.ARTS_AND_LITERATURE -> 25
        TriviaCategory.FILM_AND_TV -> 11
        TriviaCategory.MUSIC -> 12
        TriviaCategory.SPORT_AND_LEISURE -> 21
        TriviaCategory.SOCIETY_AND_CULTURE -> 24
        TriviaCategory.FOOD_AND_DRINK -> null
        else -> null
    }

    private fun mapOpenTdbCategoryName(raw: String): TriviaCategory {
        val lower = raw.lowercase()
        return when {
            "computer" in lower || "gadget" in lower || "video game" in lower -> TriviaCategory.TECHNOLOGY
            "science" in lower -> TriviaCategory.SCIENCE
            "history" in lower -> TriviaCategory.HISTORY
            "geography" in lower -> TriviaCategory.GEOGRAPHY
            "art" in lower || "book" in lower -> TriviaCategory.ARTS_AND_LITERATURE
            "film" in lower || "television" in lower -> TriviaCategory.FILM_AND_TV
            "music" in lower -> TriviaCategory.MUSIC
            "sport" in lower -> TriviaCategory.SPORT_AND_LEISURE
            "mythology" in lower || "politics" in lower -> TriviaCategory.SOCIETY_AND_CULTURE
            else -> TriviaCategory.GENERAL_KNOWLEDGE
        }
    }

    private data class VisualTopicQuery(
        val query: String,
        val category: TriviaCategory,
        val difficulty: QuizDifficulty
    )

    private fun visualTopicPoolsForLang(wikiLang: String): List<VisualTopicQuery> = when (wikiLang) {
        "es" -> listOf(
            VisualTopicQuery("Monumento Patrimonio de la Humanidad", TriviaCategory.GEOGRAPHY, QuizDifficulty.EASY),
            VisualTopicQuery("Pintura famosa museo obra arte", TriviaCategory.ARTS_AND_LITERATURE, QuizDifficulty.MEDIUM),
            VisualTopicQuery("Mamífero ave especie animal fauna", TriviaCategory.SCIENCE, QuizDifficulty.EASY),
            VisualTopicQuery("Planeta sistema solar astronomía nebulosa", TriviaCategory.SCIENCE, QuizDifficulty.MEDIUM),
            VisualTopicQuery("Volcán montaña cordillera parque nacional", TriviaCategory.GEOGRAPHY, QuizDifficulty.MEDIUM),
            VisualTopicQuery("Templo antiguo pirámide ruinas arqueológicas", TriviaCategory.HISTORY, QuizDifficulty.MEDIUM),
            VisualTopicQuery("Instrumento musical tradicional orquesta", TriviaCategory.MUSIC, QuizDifficulty.MEDIUM),
            VisualTopicQuery("Puente famoso rascacielos catedral", TriviaCategory.GEOGRAPHY, QuizDifficulty.EASY),
            VisualTopicQuery("Plato gastronómico tradicional cocina", TriviaCategory.FOOD_AND_DRINK, QuizDifficulty.EASY),
            VisualTopicQuery("Dinosaurio fósil prehistórico museo", TriviaCategory.SCIENCE, QuizDifficulty.MEDIUM)
        )
        "id" -> listOf(
            VisualTopicQuery("Situs Warisan Dunia UNESCO monumen", TriviaCategory.GEOGRAPHY, QuizDifficulty.EASY),
            VisualTopicQuery("Lukisan terkenal museum seni rupa", TriviaCategory.ARTS_AND_LITERATURE, QuizDifficulty.MEDIUM),
            VisualTopicQuery("Spesies satwa mamalia burung endemik", TriviaCategory.SCIENCE, QuizDifficulty.EASY),
            VisualTopicQuery("Planet tata surya astronomi galaksi", TriviaCategory.SCIENCE, QuizDifficulty.MEDIUM),
            VisualTopicQuery("Gunung berapi taman nasional Indonesia", TriviaCategory.GEOGRAPHY, QuizDifficulty.MEDIUM),
            VisualTopicQuery("Candi kuno piramida peninggalan sejarah", TriviaCategory.HISTORY, QuizDifficulty.MEDIUM),
            VisualTopicQuery("Alat musik tradisional gamelan orkestra", TriviaCategory.MUSIC, QuizDifficulty.MEDIUM),
            VisualTopicQuery("Jembatan terkenal menara gedung bersejarah", TriviaCategory.GEOGRAPHY, QuizDifficulty.EASY),
            VisualTopicQuery("Kuliner makanan tradisional khas daerah", TriviaCategory.FOOD_AND_DRINK, QuizDifficulty.EASY),
            VisualTopicQuery("Fosil dinosaurus prasejarah sejarah alam", TriviaCategory.SCIENCE, QuizDifficulty.MEDIUM)
        )
        else -> listOf(
            VisualTopicQuery("UNESCO World Heritage monument landmark", TriviaCategory.GEOGRAPHY, QuizDifficulty.EASY),
            VisualTopicQuery("famous painting museum oil canvas", TriviaCategory.ARTS_AND_LITERATURE, QuizDifficulty.MEDIUM),
            VisualTopicQuery("wildlife mammal bird species habitat", TriviaCategory.SCIENCE, QuizDifficulty.EASY),
            VisualTopicQuery("planet solar system nebula telescope", TriviaCategory.SCIENCE, QuizDifficulty.MEDIUM),
            VisualTopicQuery("volcano mountain national park peak", TriviaCategory.GEOGRAPHY, QuizDifficulty.MEDIUM),
            VisualTopicQuery("ancient temple pyramid archaeological site", TriviaCategory.HISTORY, QuizDifficulty.MEDIUM),
            VisualTopicQuery("musical instrument orchestra classical", TriviaCategory.MUSIC, QuizDifficulty.MEDIUM),
            VisualTopicQuery("suspension bridge skyscraper cathedral", TriviaCategory.GEOGRAPHY, QuizDifficulty.EASY),
            VisualTopicQuery("traditional dish cuisine pastry culinary", TriviaCategory.FOOD_AND_DRINK, QuizDifficulty.EASY),
            VisualTopicQuery("dinosaur fossil skeleton museum", TriviaCategory.SCIENCE, QuizDifficulty.MEDIUM)
        )
    }

    companion object {
        private const val TAG = "OnlineTriviaSources"
        private const val WIKI_USER_AGENT =
            "TriviaAtlas/1.0 (https://triviaatlas.app; contact@triviaatlas.app)"

        private val SHORT_DOMAIN_TOKENS = setOf(
            "ai", "os", "ip", "ui", "uv", "ir", "ph", "co", "ac", "dc", "tv", "3d"
        )

        private val SEARCH_STOP_WORDS = setOf(
            // English question & conceptual scaffolding
            "what", "which", "who", "whom", "whose", "when", "where", "why", "how",
            "following", "best", "most", "main", "mainly", "primary", "primarily",
            "explains", "explain", "explanation", "reason", "reasons", "purpose",
            "function", "functions", "role", "process", "cause", "causes", "caused",
            "happens", "happen", "occur", "occurs", "occurred", "result", "results",
            "statement", "statements", "describes", "describe", "correctly", "accurately",
            "true", "false", "example", "examples", "type", "types", "kind", "kinds",
            "called", "known", "named", "term", "meaning", "means", "mean", "definition",
            "according", "between", "among", "during", "within", "without", "through",
            "under", "over", "above", "below", "about", "around", "into", "from", "with",
            "does", "did", "doing", "done", "make", "makes", "made", "give", "gives", "given",
            "take", "takes", "took", "used", "using", "uses", "use", "can", "could",
            "will", "would", "should", "may", "might", "must", "need", "needs",
            "the", "and", "but", "for", "nor", "not", "yet", "this", "that", "these", "those",
            "are", "was", "were", "been", "being", "has", "have", "had", "its", "their", "they",
            "them", "you", "your", "our", "one", "two", "many", "much", "more", "less", "very",
            "year", "years", "century", "way", "ways", "because", "since", "appear", "appears",
            // Spanish question & conceptual scaffolding
            "cual", "cuál", "cuales", "cuáles", "que", "qué", "quien", "quién", "quienes", "quiénes",
            "cuando", "cuándo", "donde", "dónde", "como", "cómo", "cuanto", "cuánto", "cuantos", "cuántos",
            "por", "para", "porque", "siguiente", "siguientes", "mejor", "principal", "principalmente",
            "explica", "explicar", "explicación", "razon", "razón", "motivo", "proposito", "propósito",
            "funcion", "función", "papel", "proceso", "causa", "causan", "provoca", "ocurre", "sucede",
            "efecto", "diferencia", "afirmacion", "afirmación", "enunciado", "describe", "correctamente",
            "llama", "llamado", "conocido", "significa", "segun", "según", "entre", "durante", "sobre",
            "bajo", "desde", "hacia", "hasta", "con", "sin", "los", "las", "una", "uno", "unos", "unas",
            "del", "al", "esta", "este", "esto", "estos", "estas", "esa", "ese", "eso", "esos", "esas",
            "son", "fue", "fueron", "era", "eran", "ser", "está", "estan", "están", "tiene",
            "tienen", "puede", "pueden", "hace", "hacen", "hecho", "mas", "más", "menos", "muy",
            // Indonesian question & conceptual scaffolding
            "apa", "apakah", "siapa", "siapakah", "kapan", "kapankah", "dimana", "di", "mana",
            "mengapa", "kenapa", "bagaimana", "berapa", "manakah", "berikut", "paling",
            "tepat", "benar", "salah", "menjelaskan", "jelaskan", "penjelasan", "alasan", "sebab",
            "penyebab", "menyebabkan", "tujuan", "fungsi", "peran", "proses", "terjadi", "akibat",
            "dampak", "perbedaan", "pernyataan", "menggambarkan", "utama", "utamanya", "disebut",
            "dikenal", "artinya", "berarti", "menurut", "antara", "selama", "dalam", "pada", "dari",
            "untuk", "dengan", "tanpa", "oleh", "sebagai", "tentang", "melalui", "hingga", "sampai",
            "yang", "dan", "atau", "tetapi", "tapi", "namun", "karena", "ketika", "saat", "jika",
            "bila", "maka", "adalah", "ialah", "merupakan", "yaitu", "yakni", "bisa", "dapat",
            "akan", "telah", "sudah", "sedang", "masih", "belum", "tidak", "bukan", "ada",
            "ini", "itu", "tersebut", "mereka", "kita", "kami", "lebih", "kurang", "sangat"
        )

        private val BOOLEAN_OR_GENERIC_ANSWERS = setOf(
            "true", "false", "yes", "no", "all of the above", "none of the above",
            "verdadero", "falso", "sí", "si", "todos los anteriores", "ninguno de los anteriores",
            "benar", "salah", "ya", "tidak", "semua benar", "semua salah"
        )

        fun isPlaceholderOrFallbackExplanation(explanation: String?): Boolean {
            val trimmed = explanation?.trim().orEmpty()
            if (trimmed.isBlank()) return true
            return trimmed.startsWith("The verified answer is") ||
                trimmed.startsWith("The correct answer is") ||
                trimmed.startsWith("La respuesta verificada es") ||
                trimmed.startsWith("La respuesta correcta es") ||
                trimmed.startsWith("Jawaban terverifikasi adalah") ||
                trimmed.startsWith("Jawaban yang benar adalah") ||
                trimmed.startsWith("Key fact (") ||
                trimmed.startsWith("Dato clave (") ||
                trimmed.startsWith("Fakta singkat (") ||
                trimmed.startsWith("Fact (") ||
                trimmed.startsWith("Dato (") ||
                trimmed.startsWith("Fakta (") ||
                trimmed.startsWith("Explanation (") ||
                trimmed.startsWith("Explicación (") ||
                trimmed.startsWith("Penjelasan (")
        }

        fun isSearchableSubject(answer: String): Boolean {
            val trimmed = answer.trim()
            if (trimmed.length < 2 || trimmed.length > 48) return false
            if (trimmed.endsWith(".") || trimmed.endsWith("!") || trimmed.endsWith("?")) return false
            if (trimmed.contains(";") || trimmed.count { it == ',' } > 1) return false
            if (trimmed.all { it.isDigit() || it == '.' || it == ',' || it == '%' || it == '-' || it == '/' }) return false

            val lower = trimmed.lowercase()
            if (lower in BOOLEAN_OR_GENERIC_ANSWERS) return false

            val clauseStarters = listOf(
                "because ", "since ", "due to ", "in order ", "to ", "by ", "for ",
                "when ", "while ", "where ", "that ", "which ", "they ", "it ", "this ",
                "there ", "more ", "less ", "higher ", "lower ", "faster ", "slower ",
                "porque ", "para ", "cuando ", "donde ", "como ", "debido ", "ya que ", "al ",
                "karena ", "untuk ", "agar ", "supaya ", "ketika ", "saat ", "sebab ",
                "dengan ", "lebih ", "kurang ", "mereka "
            )
            if (clauseStarters.any { lower.startsWith(it) }) return false

            val words = trimmed.split(Regex("\\s+")).filter { it.isNotBlank() }
            if (words.size > 4) return false

            val commonClauseVerbs = setOf(
                "is", "are", "was", "were", "has", "have", "had", "can", "could", "will", "would",
                "causes", "cause", "prevents", "prevent", "allows", "allow", "makes", "make",
                "converts", "convert", "absorbs", "absorb", "reflects", "reflect", "creates", "create",
                "produces", "produce", "reduces", "reduce", "increases", "increase", "decreases", "decrease",
                "es", "son", "fue", "tiene", "tienen", "puede", "hace", "causa", "produce", "absorbe",
                "adalah", "merupakan", "membuat", "menyebabkan", "menyerap", "memantulkan", "menghasilkan"
            )
            if (words.size >= 3 && words.any { it.lowercase() in commonClauseVerbs }) return false

            return true
        }

        fun extractKeywords(text: String): List<String> {
            val cleaned = text
                .replace(Regex("[\"'“”«»?!¿¡(),.;:]"), " ")
                .replace(Regex("\\s+"), " ")
                .trim()
            if (cleaned.isBlank()) return emptyList()
            return cleaned.split(" ")
                .filter { word ->
                    val lower = word.lowercase()
                    (word.length >= 3 || lower in SHORT_DOMAIN_TOKENS) && lower !in SEARCH_STOP_WORDS
                }
                .distinctBy { it.lowercase() }
        }

        fun extractSearchTopicFromQuestion(questionText: String): String? {
            val cleaned = questionText
                .replace(Regex("[\"'“”«»?!¿¡(),.]"), " ")
                .replace(Regex("\\s+"), " ")
                .trim()
            if (cleaned.isBlank()) return null

            val tokens = cleaned.split(" ").filter { it.length >= 2 }
            val properNouns = tokens.drop(1).filter { word ->
                word.first().isUpperCase() && word.lowercase() !in SEARCH_STOP_WORDS
            }.distinctBy { it.lowercase() }

            if (properNouns.size >= 2) {
                return properNouns.take(4).joinToString(" ")
            }

            // Preserve natural sentence order for conceptual / "Why..." / "How..." / single-noun questions
            val meaningful = tokens.filter { word ->
                val lower = word.lowercase()
                (word.length >= 3 || lower in SHORT_DOMAIN_TOKENS) && lower !in SEARCH_STOP_WORDS
            }.distinctBy { it.lowercase() }

            if (properNouns.size == 1 && meaningful.isNotEmpty()) {
                val proper = properNouns.first()
                val combined = LinkedHashSet<String>()
                for (tok in tokens) {
                    if (tok.equals(proper, ignoreCase = true) || meaningful.any { it.equals(tok, ignoreCase = true) }) {
                        combined.add(tok)
                    }
                    if (combined.size >= 4) break
                }
                if (combined.isNotEmpty()) return combined.joinToString(" ")
            }

            return meaningful.take(4).joinToString(" ").takeIf { it.isNotBlank() }
        }

        fun buildWikiSearchQueries(questionText: String, correctAnswer: String): List<String> {
            val queries = LinkedHashSet<String>()
            val cleanAnswer = correctAnswer.trim()
            val searchableAnswer = isSearchableSubject(cleanAnswer)
            val qTopic = extractSearchTopicFromQuestion(questionText)
            val answerKeywords = extractKeywords(cleanAnswer)

            if (searchableAnswer) {
                queries.add(cleanAnswer)
                if (!qTopic.isNullOrBlank() && !cleanAnswer.equals(qTopic, ignoreCase = true)) {
                    queries.add("$cleanAnswer $qTopic".trim().take(80))
                    queries.add(qTopic)
                }
            } else {
                val isBooleanOrNumber = cleanAnswer.all { it.isDigit() || it in setOf('.', ',', '%', '-', '/') } ||
                    cleanAnswer.lowercase() in BOOLEAN_OR_GENERIC_ANSWERS

                if (!qTopic.isNullOrBlank() && !isBooleanOrNumber && answerKeywords.isNotEmpty()) {
                    val qWordsLower = qTopic.lowercase().split(Regex("\\s+")).toSet()
                    val novelAnswerTerms = answerKeywords.filter { it.lowercase() !in qWordsLower }.take(2)
                    if (novelAnswerTerms.isNotEmpty()) {
                        queries.add("$qTopic ${novelAnswerTerms.joinToString(" ")}")
                    }
                }
                if (!qTopic.isNullOrBlank()) {
                    queries.add(qTopic)
                }
                if (!isBooleanOrNumber && answerKeywords.size >= 2) {
                    queries.add(answerKeywords.take(4).joinToString(" "))
                }
            }
            return queries.toList()
        }

        fun buildContextualFallbackExplanation(
            questionText: String,
            answer: String,
            category: TriviaCategory,
            languageCode: String
        ): String {
            val cleanAnswer = answer.trim().replace(Regex("\\s+"), " ")
            val cleanStem = questionText
                .trim()
                .removeSuffix("?")
                .removePrefix("¿")
                .replace(Regex("\\s+"), " ")
            val isClauseAnswer = !isSearchableSubject(cleanAnswer) &&
                !cleanAnswer.all { it.isDigit() || it in setOf('.', ',', '%', '-', '/') } &&
                cleanAnswer.lowercase() !in BOOLEAN_OR_GENERIC_ANSWERS

            return if (isClauseAnswer) {
                val normalizedSentence = cleanAnswer.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }.let { if (it.endsWith(".")) it else "$it." }
                when (languageCode.lowercase()) {
                    "es" -> "Explicación (${category.title}): $normalizedSentence"
                    "in", "id" -> "Penjelasan (${category.title}): $normalizedSentence"
                    else -> "Explanation (${category.title}): $normalizedSentence"
                }
            } else {
                when (languageCode.lowercase()) {
                    "es" -> "Dato (${category.title}): $cleanAnswer — respuesta correcta para «$cleanStem»."
                    "in", "id" -> "Fakta (${category.title}): $cleanAnswer — jawaban tepat untuk \"$cleanStem\"."
                    else -> "Fact (${category.title}): $cleanAnswer — correct answer for \"$cleanStem\"."
                }
            }
        }

        fun decodeHtml(input: String): String {
            if (input.isEmpty() || !input.contains('&')) return input
            var out = input
                .replace("&quot;", "\"")
                .replace("&#039;", "'")
                .replace("&#39;", "'")
                .replace("&apos;", "'")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&ndash;", "–")
                .replace("&mdash;", "—")
                .replace("&hellip;", "…")
                .replace("&ldquo;", "“")
                .replace("&rdquo;", "”")
                .replace("&lsquo;", "‘")
                .replace("&rsquo;", "’")
                .replace("&eacute;", "é")
                .replace("&Eacute;", "É")
                .replace("&uuml;", "ü")
                .replace("&ouml;", "ö")
                .replace("&auml;", "ä")
                .replace("&ntilde;", "ñ")
                .replace("&shy;", "")
                .replace("&deg;", "°")

            out = Regex("&#(\\d+);").replace(out) { m ->
                val code = m.groupValues[1].toIntOrNull()
                if (code != null && code in 32..0x10FFFF) {
                    String(Character.toChars(code))
                } else {
                    m.value
                }
            }
            out = Regex("&#x([0-9a-fA-F]+);").replace(out) { m ->
                val code = m.groupValues[1].toIntOrNull(16)
                if (code != null && code in 32..0x10FFFF) {
                    String(Character.toChars(code))
                } else {
                    m.value
                }
            }
            return out
        }
    }
}
