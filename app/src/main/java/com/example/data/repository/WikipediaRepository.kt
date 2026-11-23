package com.example.data.repository

import android.util.Log
import com.example.data.local.LocalGeminiKeyVault
import com.example.data.local.ThemePreferences
import com.example.data.model.GeminiAvailableModel
import com.example.data.model.GeminiModelsListResponseDto
import com.example.data.model.QuizDifficulty
import com.example.data.model.QuizMode
import com.example.data.model.TriviaCategory
import com.example.data.model.TriviaQuestion
import com.example.data.model.WikipediaArticle
import com.example.data.model.WikipediaLanguageMapper
import com.example.data.remote.NetworkMonitor
import com.example.data.remote.OnlineTriviaSources
import com.example.data.remote.WikipediaApiService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import com.squareup.moshi.Moshi
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.random.Random

class WikipediaRepository(
    private val wikiApi: WikipediaApiService,
    private val networkMonitor: NetworkMonitor,
    private val themePreferences: ThemePreferences
) {
    private val inMemoryArticles = ConcurrentHashMap<String, WikipediaArticle>()
    private val inMemoryFullArticles = ConcurrentHashMap<String, WikipediaArticle>()
    private val importedQuizzes = ConcurrentHashMap<String, List<TriviaQuestion>>()

    private val geminiHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    private val webArticleHttpClient: OkHttpClient = OkHttpClient.Builder()
        .followRedirects(true)
        .followSslRedirects(true)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    fun currentDateKey(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    suspend fun getDailyCarouselArticles(
        currentUserId: String?,
        languageCode: String,
        forceRefresh: Boolean = false
    ): Result<List<WikipediaArticle>> = withContext(Dispatchers.IO) {
        val wikiLang = WikipediaLanguageMapper.toWikipediaSubdomain(languageCode)
        val todayKey = currentDateKey()
        val userScope = currentUserId ?: themePreferences.getOrCreateGuestDeviceId()

        if (!forceRefresh) {
            val cached = readCachedCarousel(userScope, wikiLang, todayKey)
            if (!cached.isNullOrEmpty()) {
                cached.forEach { inMemoryArticles[articleKey(it.wikiLang, it.pageId)] = it }
                return@withContext Result.success(cached)
            }
        }

        if (!networkMonitor.checkCurrentConnectivity()) {
            return@withContext Result.failure(IllegalStateException("OFFLINE"))
        }

        try {
            val seenIds = themePreferences.getSeenWikiPageIds(userScope)
            val endpoint = WikipediaApiService.apiUrlForSubdomain(wikiLang)
            val candidates = LinkedHashMap<Long, WikipediaArticle>()

            repeat(3) { attempt ->
                if (candidates.count { it.value.thumbnailUrl != null } >= TARGET_CAROUSEL_COUNT &&
                    candidates.size >= TARGET_CAROUSEL_COUNT
                ) {
                    return@repeat
                }
                val response = wikiApi.getRandomArticles(endpointUrl = endpoint)
                val pages = response.query?.pages.orEmpty()
                for (page in pages) {
                    val title = page.title?.trim().orEmpty()
                    val extract = page.extract?.trim().orEmpty()
                    if (page.pageId <= 0L || title.isBlank() || extract.length < 75) continue
                    if (page.pageId in seenIds && attempt == 0) continue

                    val rawThumb = page.thumbnail?.source?.trim()?.takeIf { url ->
                        url.startsWith("http") &&
                            !url.endsWith(".svg", ignoreCase = true) &&
                            !url.endsWith(".ogg", ignoreCase = true) &&
                            !url.endsWith(".webm", ignoreCase = true)
                    }

                    val article = WikipediaArticle(
                        pageId = page.pageId,
                        title = title,
                        description = page.description?.trim()?.takeIf { it.isNotBlank() },
                        extract = extract,
                        thumbnailUrl = rawThumb,
                        articleUrl = page.fullUrl,
                        wikiLang = wikiLang,
                        dateKey = todayKey
                    )
                    candidates.putIfAbsent(article.pageId, article)
                }
            }

            val selected = candidates.values
                .sortedWith(
                    compareByDescending<WikipediaArticle> { it.thumbnailUrl != null }
                        .thenByDescending { it.extract.length }
                )
                .take(TARGET_CAROUSEL_COUNT)

            if (selected.isEmpty()) {
                return@withContext Result.failure(IllegalStateException("No random Wikipedia articles returned"))
            }

            saveCachedCarousel(userScope, wikiLang, todayKey, selected)
            themePreferences.addSeenWikiPageIds(userScope, selected.map { it.pageId })
            selected.forEach { inMemoryArticles[articleKey(it.wikiLang, it.pageId)] = it }

            Result.success(selected)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            runCatching { Log.e(TAG, "Failed to fetch random Wikipedia articles for $wikiLang", e) }
            Result.failure(e)
        }
    }

    suspend fun findArticle(
        currentUserId: String?,
        wikiLang: String,
        pageId: Long
    ): WikipediaArticle? = withContext(Dispatchers.IO) {
        val key = articleKey(wikiLang, pageId)
        inMemoryFullArticles[key]?.let { return@withContext it }
        inMemoryArticles[key]?.let { return@withContext it }
        val userScope = currentUserId ?: themePreferences.getOrCreateGuestDeviceId()
        val cached = readCachedCarousel(userScope, wikiLang, dateFilter = null)
        cached?.firstOrNull { it.pageId == pageId }?.also {
            inMemoryArticles[key] = it
        }
    }

    fun cacheArticleInMemory(article: WikipediaArticle) {
        val key = articleKey(article.wikiLang, article.pageId)
        inMemoryArticles[key] = article
        if (article.pageId < 0L) {
            inMemoryFullArticles[key] = article
        }
    }

    suspend fun searchWikipediaArticles(
        query: String,
        languageCode: String
    ): Result<List<WikipediaArticle>> = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.length < 2) return@withContext Result.success(emptyList())
        if (!networkMonitor.checkCurrentConnectivity()) {
            return@withContext Result.failure(IllegalStateException("OFFLINE"))
        }
        val wikiLang = WikipediaLanguageMapper.toWikipediaSubdomain(languageCode)
        val todayKey = currentDateKey()
        try {
            val endpoint = WikipediaApiService.apiUrlForSubdomain(wikiLang)
            val response = wikiApi.searchArticles(endpointUrl = endpoint, searchQuery = trimmed)
            val pages = response.query?.pages.orEmpty()
            val mapped = pages.mapNotNull { page ->
                val title = page.title?.trim().orEmpty()
                if (page.pageId <= 0L || title.isBlank()) return@mapNotNull null
                val rawThumb = page.thumbnail?.source?.trim()?.takeIf { url ->
                    url.startsWith("http") &&
                        !url.endsWith(".svg", ignoreCase = true) &&
                        !url.endsWith(".ogg", ignoreCase = true) &&
                        !url.endsWith(".webm", ignoreCase = true)
                }
                WikipediaArticle(
                    pageId = page.pageId,
                    title = title,
                    description = page.description?.trim()?.takeIf { it.isNotBlank() },
                    extract = page.extract?.trim().orEmpty().ifBlank {
                        page.description?.trim().orEmpty()
                    },
                    thumbnailUrl = rawThumb,
                    articleUrl = page.fullUrl,
                    wikiLang = wikiLang,
                    dateKey = todayKey
                ).also { article ->
                    inMemoryArticles[articleKey(article.wikiLang, article.pageId)] = article
                }
            }
            Result.success(mapped)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            runCatching { Log.e(TAG, "Failed to search Wikipedia for '$trimmed' ($wikiLang)", e) }
            Result.failure(e)
        }
    }

    suspend fun loadCustomArticleFromUrl(
        rawUrl: String,
        languageCode: String
    ): Result<WikipediaArticle> = withContext(Dispatchers.IO) {
        val normalizedUrl = normalizeWebUrl(rawUrl)
            ?: return@withContext Result.failure(IllegalStateException("INVALID_URL"))

        if (!networkMonitor.checkCurrentConnectivity()) {
            return@withContext Result.failure(IllegalStateException("OFFLINE"))
        }

        val todayKey = currentDateKey()
        val wikiTarget = extractWikipediaUrlTarget(normalizedUrl)
        if (wikiTarget != null) {
            val (subdomain, wikiTitle) = wikiTarget
            try {
                val endpoint = WikipediaApiService.apiUrlForSubdomain(subdomain)
                val response = wikiApi.getArticleByTitle(
                    endpointUrl = endpoint,
                    titles = wikiTitle
                )
                val page = response.query?.pages?.firstOrNull()
                val extract = page?.extract?.trim().orEmpty()
                if (page != null && page.pageId > 0L && extract.length >= 40) {
                    val rawThumb = page.thumbnail?.source?.trim()?.takeIf { url ->
                        url.startsWith("http") && !url.endsWith(".svg", ignoreCase = true)
                    }
                    val article = WikipediaArticle(
                        pageId = page.pageId,
                        title = page.title?.trim()?.ifBlank { wikiTitle } ?: wikiTitle,
                        description = page.description?.trim()?.takeIf { it.isNotBlank() },
                        extract = extract,
                        thumbnailUrl = rawThumb,
                        articleUrl = page.fullUrl ?: normalizedUrl,
                        wikiLang = subdomain,
                        dateKey = todayKey
                    )
                    val key = articleKey(article.wikiLang, article.pageId)
                    inMemoryFullArticles[key] = article
                    inMemoryArticles[key] = article
                    return@withContext Result.success(article)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                runCatching { Log.w(TAG, "Wikipedia URL lookup fell back to HTML fetch", e) }
            }
        }

        try {
            val request = Request.Builder()
                .url(normalizedUrl)
                .header(
                    "User-Agent",
                    "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36"
                )
                .header("Accept", "text/html,application/xhtml+xml,text/plain;q=0.9,*/*;q=0.8")
                .get()
                .build()

            webArticleHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(IllegalStateException("HTTP_${response.code}"))
                }
                val contentType = response.header("Content-Type").orEmpty().lowercase(Locale.US)
                if (contentType.isNotBlank() &&
                    !contentType.contains("html") &&
                    !contentType.contains("text") &&
                    !contentType.contains("xml")
                ) {
                    return@withContext Result.failure(IllegalStateException("ARTICLE_NOT_READABLE"))
                }
                val html = response.body?.string()?.take(1_500_000).orEmpty()
                val finalUrl = response.request.url.toString()
                val fallbackLang = WikipediaLanguageMapper.toWikipediaSubdomain(languageCode)
                val parsed = extractReadableWebArticle(
                    html = html,
                    sourceUrl = finalUrl,
                    languageCode = fallbackLang,
                    dateKey = todayKey
                ) ?: return@withContext Result.failure(IllegalStateException("ARTICLE_NOT_READABLE"))

                val key = articleKey(parsed.wikiLang, parsed.pageId)
                inMemoryFullArticles[key] = parsed
                inMemoryArticles[key] = parsed
                Result.success(parsed)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            runCatching { Log.e(TAG, "Failed to load custom article URL", e) }
            Result.failure(e)
        }
    }

    suspend fun getFullArticleContent(
        currentUserId: String?,
        wikiLang: String,
        pageId: Long,
        fallbackTitle: String? = null
    ): Result<WikipediaArticle> = withContext(Dispatchers.IO) {
        val key = articleKey(wikiLang, pageId)
        inMemoryFullArticles[key]?.let { return@withContext Result.success(it) }

        val summaryArticle = findArticle(currentUserId, wikiLang, pageId)

        // Custom web URLs use negative synthetic IDs and already hold their full readable text in memory
        if (pageId < 0L) {
            return@withContext if (summaryArticle != null) {
                Result.success(summaryArticle)
            } else {
                Result.failure(IllegalStateException("ARTICLE_NOT_FOUND"))
            }
        }

        if (!networkMonitor.checkCurrentConnectivity()) {
            return@withContext if (summaryArticle != null) {
                Result.success(summaryArticle)
            } else {
                Result.failure(IllegalStateException("OFFLINE"))
            }
        }

        try {
            val endpoint = WikipediaApiService.apiUrlForSubdomain(wikiLang)
            val response = wikiApi.getFullArticle(endpointUrl = endpoint, pageId = pageId)
            val page = response.query?.pages?.firstOrNull()
            val fullExtract = page?.extract?.trim()

            if (!fullExtract.isNullOrBlank()) {
                val resolvedThumb = page.thumbnail?.source?.trim()?.takeIf { url ->
                    url.startsWith("http") && !url.endsWith(".svg", ignoreCase = true)
                } ?: summaryArticle?.thumbnailUrl

                val fullArticle = WikipediaArticle(
                    pageId = pageId,
                    title = page.title?.trim()?.takeIf { it.isNotBlank() }
                        ?: summaryArticle?.title
                        ?: fallbackTitle
                        ?: "Wikipedia",
                    description = page.description?.trim()?.takeIf { it.isNotBlank() }
                        ?: summaryArticle?.description,
                    extract = fullExtract,
                    thumbnailUrl = resolvedThumb,
                    articleUrl = page.fullUrl ?: summaryArticle?.articleUrl,
                    wikiLang = wikiLang,
                    dateKey = summaryArticle?.dateKey ?: currentDateKey()
                )
                inMemoryFullArticles[key] = fullArticle
                inMemoryArticles[key] = fullArticle
                return@withContext Result.success(fullArticle)
            }

            if (summaryArticle != null) {
                Result.success(summaryArticle)
            } else {
                Result.failure(IllegalStateException("ARTICLE_NOT_FOUND"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            runCatching { Log.e(TAG, "Failed to load full Wikipedia article $pageId ($wikiLang)", e) }
            if (summaryArticle != null) {
                Result.success(summaryArticle)
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * Builds a ready-to-paste prompt containing the Wikipedia article and exact JSON schema
     * so users can run it in their own AI chat app (Gemini, ChatGPT, Claude, etc.) for free.
     */
    fun buildUserAiQuizPrompt(
        article: WikipediaArticle,
        languageCode: String,
        difficulty: QuizDifficulty,
        mode: QuizMode,
        requestedCount: Int
    ): String {
        val count = requestedCount.coerceIn(5, 15)
        val targetLanguage = WikipediaLanguageMapper.targetLanguageName(languageCode)
        val articleBody = article.extract.take(6000)

        val difficultyInstruction = when (difficulty) {
            QuizDifficulty.EASY -> "Target EASY trivia questions focusing on clear facts in the article."
            QuizDifficulty.MEDIUM -> "Target MEDIUM trivia questions balancing main themes and notable details."
            QuizDifficulty.HARD -> "Target HARD trivia questions testing specific dates, names, or facts in the article."
            QuizDifficulty.ALL -> "Vary difficulty across easy, medium, and hard questions."
        }
        val modeInstruction = if (mode == QuizMode.GUESS) {
            "Because the player will TYPE their answer in Guess Mode, keep every correctAnswer short (1 to 4 words, no punctuation), while still providing 3 plausible incorrectAnswers."
        } else {
            "Provide 3 plausible, clearly distinct incorrectAnswers for each question."
        }

        return buildString {
            appendLine("You are an expert trivia quiz author for an educational encyclopedia app.")
            appendLine("Generate exactly $count accurate trivia questions grounded strictly in facts from this Wikipedia article.")
            appendLine("CRITICAL LANGUAGE REQUIREMENT: Write ALL text (questionText, correctAnswer, all 3 incorrectAnswers, and explanation) 100% in $targetLanguage.")
            appendLine(difficultyInstruction)
            appendLine(modeInstruction)
            appendLine("Do not ask meta-questions about Wikipedia itself; focus on the real subject.")
            appendLine()
            appendLine("Return ONLY valid JSON matching this exact shape (no extra prose):")
            appendLine(
                """{"questions":[{"questionText":"...","correctAnswer":"...","incorrectAnswers":["...","...","..."],"difficulty":"medium","explanation":"1-2 sentence fact explanation from the article"}]}"""
            )
            appendLine()
            appendLine("Article Title: ${article.title}")
            if (!article.description.isNullOrBlank()) {
                appendLine("Summary Tag: ${article.description}")
            }
            appendLine("Article Text:")
            appendLine(articleBody)
        }
    }

    fun importCustomAiQuizJson(
        article: WikipediaArticle,
        rawResponse: String
    ): Result<List<TriviaQuestion>> {
        return try {
            val cleaned = stripMarkdownCodeFences(rawResponse)
            val jsonPayload = extractJsonObjectOrArray(cleaned)
                ?: return Result.failure(IllegalStateException("INVALID_AI_JSON"))
            val rng = Random(System.nanoTime() xor article.pageId)
            val parsed = parseQuestionsFromJson(jsonPayload, article, rng)
            if (parsed.isEmpty()) {
                Result.failure(IllegalStateException("INVALID_AI_JSON"))
            } else {
                importedQuizzes[articleKey(article.wikiLang, article.pageId)] = parsed
                Result.success(parsed)
            }
        } catch (_: Exception) {
            Result.failure(IllegalStateException("INVALID_AI_JSON"))
        }
    }

    data class WikiQuizGenerationResult(
        val questions: List<TriviaQuestion>,
        val usedGeminiAi: Boolean,
        val aiModelDisplayName: String? = null,
        val fellBackToInstant: Boolean = false
    )

    suspend fun generateQuizForArticle(
        article: WikipediaArticle,
        languageCode: String,
        difficulty: QuizDifficulty,
        mode: QuizMode,
        requestedCount: Int,
        sourceMode: String = SOURCE_AUTO
    ): Result<List<TriviaQuestion>> =
        generateQuizForArticleWithMeta(
            article = article,
            languageCode = languageCode,
            difficulty = difficulty,
            mode = mode,
            requestedCount = requestedCount,
            sourceMode = sourceMode
        ).map { it.questions }

    suspend fun generateQuizForArticleWithMeta(
        article: WikipediaArticle,
        languageCode: String,
        difficulty: QuizDifficulty,
        mode: QuizMode,
        requestedCount: Int,
        sourceMode: String = SOURCE_AUTO
    ): Result<WikiQuizGenerationResult> = withContext(Dispatchers.IO) {
        val key = articleKey(article.wikiLang, article.pageId)

        if (sourceMode == SOURCE_IMPORTED) {
            val imported = importedQuizzes[key]
            return@withContext if (!imported.isNullOrEmpty()) {
                Result.success(
                    WikiQuizGenerationResult(
                        questions = imported,
                        usedGeminiAi = false,
                        fellBackToInstant = false
                    )
                )
            } else {
                Result.failure(IllegalStateException("INVALID_AI_JSON"))
            }
        }

        val fullArticle = getFullArticleContent(
            currentUserId = null,
            wikiLang = article.wikiLang,
            pageId = article.pageId,
            fallbackTitle = article.title
        ).getOrElse { article }

        val count = requestedCount.coerceIn(5, 15)

        if (sourceMode == SOURCE_LOCAL) {
            val localQuiz = generateLocalQuizForArticle(fullArticle, languageCode, difficulty, mode, count)
            return@withContext if (localQuiz.isNotEmpty()) {
                Result.success(
                    WikiQuizGenerationResult(
                        questions = localQuiz,
                        usedGeminiAi = false,
                        fellBackToInstant = false
                    )
                )
            } else {
                Result.failure(IllegalStateException("Unable to build article quiz."))
            }
        }

        // Personal Gemini API key stays strictly on-device and calls Google Gemini directly with selected model
        val customKey = themePreferences.customGeminiApiKey.first()?.trim().orEmpty()
        if (customKey.isBlank()) {
            if (sourceMode == SOURCE_AUTO) {
                val localQuiz = generateLocalQuizForArticle(fullArticle, languageCode, difficulty, mode, count)
                if (localQuiz.isNotEmpty()) {
                    return@withContext Result.success(
                        WikiQuizGenerationResult(
                            questions = localQuiz,
                            usedGeminiAi = false,
                            fellBackToInstant = false
                        )
                    )
                }
            }
            return@withContext Result.failure(IllegalStateException("MISSING_CUSTOM_API_KEY"))
        }

        var selectedModelId = themePreferences.customGeminiModelId.first()?.trim().orEmpty()
        var selectedModelName = themePreferences.customGeminiModelName.first()?.trim().orEmpty()
        val rng = Random(System.nanoTime() xor fullArticle.pageId)

        if (networkMonitor.checkCurrentConnectivity()) {
            if (selectedModelId.isBlank()) {
                val discovered = fetchAvailableGeminiModels(customKey).getOrNull()?.firstOrNull()
                if (discovered != null) {
                    selectedModelId = discovered.modelId
                    selectedModelName = discovered.displayName
                    themePreferences.setCustomGeminiModel(discovered.modelId, discovered.displayName)
                }
            }

            if (selectedModelId.isNotBlank()) {
                val prompt = buildUserAiQuizPrompt(fullArticle, languageCode, difficulty, mode, count)
                try {
                    val rawText = callGeminiRestApi(
                        apiKey = customKey,
                        modelName = selectedModelId,
                        prompt = prompt
                    )
                    val cleaned = stripMarkdownCodeFences(rawText)
                    val jsonPayload = extractJsonObjectOrArray(cleaned) ?: cleaned
                    val parsed = parseQuestionsFromJson(jsonPayload, fullArticle, rng)
                    if (parsed.isNotEmpty()) {
                        return@withContext Result.success(
                            WikiQuizGenerationResult(
                                questions = parsed,
                                usedGeminiAi = true,
                                aiModelDisplayName = selectedModelName.ifBlank { selectedModelId },
                                fellBackToInstant = false
                            )
                        )
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    val safeMsg = LocalGeminiKeyVault.redactSecret(e.message, customKey)
                    runCatching { Log.w(TAG, "Personal Gemini REST call failed on $selectedModelId: $safeMsg") }
                }
            }
        }

        // Automatic fallback to Instant Article Quiz whenever AI generation fails or is offline
        val localFallbackQuiz = generateLocalQuizForArticle(fullArticle, languageCode, difficulty, mode, count)
        if (localFallbackQuiz.isNotEmpty()) {
            return@withContext Result.success(
                WikiQuizGenerationResult(
                    questions = localFallbackQuiz,
                    usedGeminiAi = false,
                    aiModelDisplayName = null,
                    fellBackToInstant = true
                )
            )
        }

        Result.failure(
            IllegalStateException("Unable to generate trivia quiz from this article.")
        )
    }

    suspend fun fetchAvailableGeminiModels(
        apiKey: String
    ): Result<List<GeminiAvailableModel>> = withContext(Dispatchers.IO) {
        val cleanedKey = apiKey.trim()
        if (cleanedKey.isBlank()) {
            return@withContext Result.success(emptyList())
        }
        if (!networkMonitor.checkCurrentConnectivity()) {
            return@withContext Result.failure(IllegalStateException("OFFLINE"))
        }

        try {
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models?pageSize=100")
                .header("x-goog-api-key", cleanedKey)
                .get()
                .build()

            geminiHttpClient.newCall(request).execute().use { response ->
                val raw = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    val apiMsg = runCatching {
                        JSONObject(raw).optJSONObject("error")?.optString("message")
                    }.getOrNull()?.takeIf { it.isNotBlank() }
                        ?: "Gemini API HTTP ${response.code}"
                    return@withContext Result.failure(
                        IllegalStateException(LocalGeminiKeyVault.redactSecret(apiMsg, cleanedKey))
                    )
                }
                val models = parseAvailableModelsJson(raw)
                Result.success(models)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val safeMsg = LocalGeminiKeyVault.redactSecret(e.message, cleanedKey)
            Result.failure(IllegalStateException(safeMsg))
        }
    }

    private fun callGeminiRestApi(
        apiKey: String,
        modelName: String,
        prompt: String
    ): String {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent"
        val bodyJson = JSONObject().apply {
            put(
                "contents",
                JSONArray().put(
                    JSONObject().put(
                        "parts",
                        JSONArray().put(JSONObject().put("text", prompt))
                    )
                )
            )
            put(
                "generationConfig",
                JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.65)
                }
            )
        }

        val request = Request.Builder()
            .url(url)
            .header("x-goog-api-key", apiKey)
            .post(bodyJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        geminiHttpClient.newCall(request).execute().use { response ->
            val raw = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                val apiMsg = runCatching {
                    JSONObject(raw).optJSONObject("error")?.optString("message")
                }.getOrNull()?.takeIf { it.isNotBlank() }
                    ?: "Gemini API HTTP ${response.code}"
                throw IllegalStateException(LocalGeminiKeyVault.redactSecret(apiMsg, apiKey))
            }
            val root = JSONObject(raw)
            val candidates = root.optJSONArray("candidates")
                ?: throw IllegalStateException("Empty Gemini response")
            val firstCandidate = candidates.optJSONObject(0)
                ?: throw IllegalStateException("No candidate returned by Gemini")
            val parts = firstCandidate.optJSONObject("content")?.optJSONArray("parts")
                ?: throw IllegalStateException("No text content in Gemini response")
            return buildString {
                for (i in 0 until parts.length()) {
                    append(parts.optJSONObject(i)?.optString("text").orEmpty())
                }
            }
        }
    }

    internal fun generateLocalQuizForArticle(
        article: WikipediaArticle,
        languageCode: String,
        difficulty: QuizDifficulty,
        mode: QuizMode,
        requestedCount: Int
    ): List<TriviaQuestion> {
        val normLang = when (languageCode.lowercase()) {
            "es" -> "es"
            "in", "id" -> "in"
            else -> "en"
        }
        val rng = Random(System.nanoTime() xor article.pageId)
        val count = requestedCount.coerceIn(5, 12)
        val rawSentences = article.extract
            .replace(Regex("={2,4}.*?={2,4}"), " ")
            .replace(Regex("\\s+"), " ")
            .split(Regex("(?<=[.!?])\\s+"))
            .map { it.trim() }
            .filter { it.length in 40..280 }

        val targetDiff = if (difficulty == QuizDifficulty.ALL) QuizDifficulty.MEDIUM else difficulty
        val questions = ArrayList<TriviaQuestion>(count)
        val usedPrompts = HashSet<String>()
        val usedAnswers = HashSet<String>()

        // Collect all capitalized multi-word or notable single-word terms across the article to serve as believable distractors
        val articleTerms = extractNotableTerms(article.extract, excludeTitle = article.title)
        val articleYears = Regex("\\b(1[4-9]\\d{2}|20[0-2]\\d)\\b")
            .findAll(article.extract)
            .map { it.value }
            .distinct()
            .toList()

        // 1. Definition / identity question from the opening description or lead sentence
        val leadClue = article.description?.trim()?.takeIf { it.length in 10..110 }
            ?: rawSentences.firstOrNull()?.let { first ->
                maskTarget(first, article.title)
            }
        if (!leadClue.isNullOrBlank()) {
            val titleDistractors = (articleTerms.filter { !it.equals(article.title, ignoreCase = true) } +
                localizedGeneralSubjects(normLang))
                .distinctBy { it.lowercase() }
                .shuffled(rng)
                .take(3)
            if (titleDistractors.size == 3 && usedAnswers.add(article.title.lowercase())) {
                val qText = when (normLang) {
                    "es" -> "¿Qué tema se describe en el artículo como: \"$leadClue\"?"
                    "in" -> "Topik manakah yang dijelaskan dalam artikel sebagai: \"$leadClue\"?"
                    else -> "Which subject is described in this article as: \"$leadClue\"?"
                }
                usedPrompts.add(qText)
                questions.add(
                    TriviaQuestion(
                        id = "wiki_local_${article.wikiLang}_${article.pageId}_0",
                        category = TriviaCategory.WIKIPEDIA,
                        difficulty = QuizDifficulty.EASY,
                        questionText = qText,
                        correctAnswer = article.title,
                        allOptions = (titleDistractors + article.title).shuffled(rng),
                        pictureQuestion = false,
                        imageUrl = article.thumbnailUrl,
                        illustrationTag = article.title,
                        explanation = rawSentences.firstOrNull() ?: article.description ?: article.title
                    )
                )
            }
        }

        // 2. Fact-extraction questions from factual sentences (dates & notable terms)
        val yearRegex = Regex("\\b(1[4-9]\\d{2}|20[0-2]\\d)\\b")
        val numberRegex = Regex("\\b([1-9]\\d{0,3})\\b")

        for (sentence in rawSentences.shuffled(rng)) {
            if (questions.size >= count) break

            val yearMatch = yearRegex.find(sentence)
            if (yearMatch != null) {
                val answerYear = yearMatch.value
                if (usedAnswers.add(answerYear)) {
                    val blanked = sentence.replaceFirst(answerYear, "____")
                    if (usedPrompts.add(blanked) && blanked.length > 25) {
                        val distractors = buildYearDistractors(answerYear.toInt(), articleYears, rng)
                        val qText = when (normLang) {
                            "es" -> "Completa el dato del artículo: $blanked"
                            "in" -> "Lengkapi tahun fakta dari artikel ini: $blanked"
                            else -> "Fill in the missing year from the article: $blanked"
                        }
                        questions.add(
                            TriviaQuestion(
                                id = "wiki_local_${article.wikiLang}_${article.pageId}_${questions.size}",
                                category = TriviaCategory.WIKIPEDIA,
                                difficulty = targetDiff,
                                questionText = qText,
                                correctAnswer = answerYear,
                                allOptions = (distractors + answerYear).shuffled(rng),
                                pictureQuestion = false,
                                imageUrl = article.thumbnailUrl,
                                illustrationTag = article.title,
                                explanation = sentence
                            )
                        )
                        continue
                    }
                }
            }

            // Look for an entity or proper noun in the middle of the sentence
            val candidateTerm = extractNotableTerms(sentence, excludeTitle = article.title)
                .firstOrNull { term ->
                    term.length in 4..34 &&
                        !sentence.startsWith(term, ignoreCase = true) &&
                        usedAnswers.add(term.lowercase()) &&
                        (mode != QuizMode.GUESS || term.split(" ").size <= 3)
                }

            if (candidateTerm != null) {
                val blanked = sentence.replaceFirst(candidateTerm, "_____")
                if (blanked != sentence && usedPrompts.add(blanked)) {
                    val distractors = (articleTerms.filter { !it.equals(candidateTerm, ignoreCase = true) } +
                        localizedGeneralSubjects(normLang))
                        .distinctBy { it.lowercase() }
                        .filter { !it.equals(candidateTerm, ignoreCase = true) }
                        .shuffled(rng)
                        .take(3)
                    if (distractors.size == 3) {
                        val qText = when (normLang) {
                            "es" -> "Según el artículo, ¿qué término completa la afirmación?\n\"$blanked\""
                            "in" -> "Menurut artikel, istilah apa yang melengkapi kalimat ini?\n\"$blanked\""
                            else -> "According to the article, what completes this statement?\n\"$blanked\""
                        }
                        questions.add(
                            TriviaQuestion(
                                id = "wiki_local_${article.wikiLang}_${article.pageId}_${questions.size}",
                                category = TriviaCategory.WIKIPEDIA,
                                difficulty = targetDiff,
                                questionText = qText,
                                correctAnswer = candidateTerm,
                                allOptions = (distractors + candidateTerm).shuffled(rng),
                                pictureQuestion = false,
                                imageUrl = article.thumbnailUrl,
                                illustrationTag = article.title,
                                explanation = sentence
                            )
                        )
                        continue
                    }
                }
            }

            // Numeric fact (population, length, count)
            val numMatch = numberRegex.find(sentence)
            if (numMatch != null) {
                val numVal = numMatch.value.toIntOrNull()
                if (numVal != null && numVal in 2..5000 && usedAnswers.add(numVal.toString())) {
                    val blanked = sentence.replaceFirst(Regex("\\b$numVal\\b"), "____")
                    if (blanked != sentence && usedPrompts.add(blanked)) {
                        val numDistractors = buildNumericDistractors(numVal, rng)
                        val qText = when (normLang) {
                            "es" -> "¿Qué número aparece en este dato del artículo?\n\"$blanked\""
                            "in" -> "Angka berapa yang tepat untuk fakta dalam artikel ini?\n\"$blanked\""
                            else -> "Which number completes this fact from the article?\n\"$blanked\""
                        }
                        questions.add(
                            TriviaQuestion(
                                id = "wiki_local_${article.wikiLang}_${article.pageId}_${questions.size}",
                                category = TriviaCategory.WIKIPEDIA,
                                difficulty = targetDiff,
                                questionText = qText,
                                correctAnswer = numVal.toString(),
                                allOptions = (numDistractors + numVal.toString()).shuffled(rng),
                                pictureQuestion = false,
                                imageUrl = article.thumbnailUrl,
                                illustrationTag = article.title,
                                explanation = sentence
                            )
                        )
                    }
                }
            }
        }

        // 3. True/False comprehension questions in target language to fill remaining requested count cleanly
        var tfIdx = 0
        while (questions.size < count && tfIdx < rawSentences.size) {
            val factSentence = rawSentences[tfIdx]
            tfIdx++
            if (usedPrompts.contains(factSentence)) continue
            usedPrompts.add(factSentence)

            val answerTrue = localizedBooleanTrue(normLang)
            val answerFalse = localizedBooleanFalse(normLang)
            val unsure = localizedBooleanUnknown(normLang)
            val qText = when (normLang) {
                "es" -> "Verdadero o Falso según \"${article.title}\":\n\"$factSentence\""
                "in" -> "Benar atau Salah berdasarkan artikel \"${article.title}\":\n\"$factSentence\""
                else -> "True or False according to \"${article.title}\":\n\"$factSentence\""
            }
            questions.add(
                TriviaQuestion(
                    id = "wiki_local_${article.wikiLang}_${article.pageId}_${questions.size}",
                    category = TriviaCategory.WIKIPEDIA,
                    difficulty = QuizDifficulty.EASY,
                    questionText = qText,
                    correctAnswer = answerTrue,
                    allOptions = listOf(answerTrue, answerFalse, unsure).shuffled(rng),
                    pictureQuestion = false,
                    imageUrl = article.thumbnailUrl,
                    illustrationTag = article.title,
                    explanation = factSentence
                )
            )
        }

        return questions
    }

    private fun extractNotableTerms(text: String, excludeTitle: String): List<String> {
        val stopWords = setOf(
            "the", "and", "for", "from", "with", "this", "that", "after", "before", "during",
            "when", "where", "while", "until", "since", "because", "although", "however",
            "las", "los", "una", "uno", "con", "para", "por", "como", "pero", "sobre", "entre",
            "yang", "dan", "dari", "untuk", "pada", "dalam", "dengan", "adalah", "oleh", "atau"
        )
        val titleLower = excludeTitle.lowercase()
        val matches = Regex("\\b[A-Z][a-zA-ZÀ-ÿ0-9'’-]{2,}(?:\\s+[A-Z][a-zA-ZÀ-ÿ0-9'’-]{2,}){0,2}\\b")
            .findAll(text)
            .map { it.value.trim() }
            .filter { token ->
                val lower = token.lowercase()
                lower != titleLower &&
                    !titleLower.contains(lower) &&
                    lower !in stopWords &&
                    token.length in 4..34
            }
            .distinctBy { it.lowercase() }
            .toList()
        return matches
    }

    private fun maskTarget(sentence: String, target: String): String {
        var masked = sentence.replace(Regex(Regex.escape(target), RegexOption.IGNORE_CASE), "_____")
        target.split(Regex("\\s+")).filter { it.length >= 4 }.forEach { part ->
            masked = masked.replace(Regex("\\b${Regex.escape(part)}\\b", RegexOption.IGNORE_CASE), "_____")
        }
        return masked.trim()
    }

    private fun buildYearDistractors(year: Int, articleYears: List<String>, rng: Random): List<String> {
        val candidates = LinkedHashSet<String>()
        articleYears.filter { it != year.toString() }.shuffled(rng).forEach { candidates.add(it) }
        val offsets = listOf(-12, -5, -3, 2, 4, 8, 15, -25).shuffled(rng)
        for (offset in offsets) {
            if (candidates.size >= 3) break
            val y = (year + offset).coerceIn(1000, 2025)
            if (y != year) candidates.add(y.toString())
        }
        return candidates.take(3)
    }

    private fun buildNumericDistractors(value: Int, rng: Random): List<String> {
        val candidates = LinkedHashSet<String>()
        val deltas = listOf(-4, -2, -1, 1, 2, 3, 5, 10).shuffled(rng)
        for (d in deltas) {
            if (candidates.size >= 3) break
            val n = value + d
            if (n > 0 && n != value) candidates.add(n.toString())
        }
        return candidates.take(3)
    }

    private fun localizedGeneralSubjects(normLang: String): List<String> = when (normLang) {
        "es" -> listOf("Imperio romano", "Revolución Industrial", "Monte Everest", "Museo del Louvre", "Océano Atlántico", "Renacimiento")
        "in" -> listOf("Kekaisaran Romawi", "Revolusi Industri", "Gunung Everest", "Museum Louvre", "Samudra Atlantik", "Zaman Renaisans")
        else -> listOf("Roman Empire", "Industrial Revolution", "Mount Everest", "Louvre Museum", "Atlantic Ocean", "Renaissance Era")
    }

    private fun localizedBooleanTrue(normLang: String): String = when (normLang) {
        "es" -> "Verdadero"
        "in" -> "Benar"
        else -> "True"
    }

    private fun localizedBooleanFalse(normLang: String): String = when (normLang) {
        "es" -> "Falso"
        "in" -> "Salah"
        else -> "False"
    }

    private fun localizedBooleanUnknown(normLang: String): String = when (normLang) {
        "es" -> "No mencionado en el artículo"
        "in" -> "Tidak disebutkan dalam artikel"
        else -> "Not mentioned in the article"
    }

    private fun stripMarkdownCodeFences(raw: String): String {
        var text = raw.trim()
        if (text.startsWith("```")) {
            text = text.removePrefix("```json").removePrefix("```").trim()
        }
        if (text.endsWith("```")) {
            text = text.removeSuffix("```").trim()
        }
        return text
    }

    private fun extractJsonObjectOrArray(raw: String): String? {
        val objStart = raw.indexOf('{')
        val objEnd = raw.lastIndexOf('}')
        val arrStart = raw.indexOf('[')
        val arrEnd = raw.lastIndexOf(']')
        return when {
            objStart >= 0 && objEnd > objStart && (arrStart < 0 || objStart < arrStart) ->
                raw.substring(objStart, objEnd + 1)
            arrStart >= 0 && arrEnd > arrStart ->
                raw.substring(arrStart, arrEnd + 1)
            else -> null
        }
    }

    internal fun parseQuestionsFromJson(
        jsonText: String,
        article: WikipediaArticle,
        rng: Random
    ): List<TriviaQuestion> {
        val trimmed = jsonText.trim()
        if (trimmed.isBlank()) return emptyList()
        val arr = if (trimmed.startsWith("[")) {
            JSONArray(trimmed)
        } else {
            val root = JSONObject(trimmed)
            root.optJSONArray("questions") ?: return emptyList()
        }
        val parsed = ArrayList<TriviaQuestion>(arr.length())

        for (i in 0 until arr.length()) {
            val obj = arr.optJSONObject(i) ?: continue
            val qText = obj.optString("questionText").ifBlank { obj.optString("question") }.trim()
            val correct = obj.optString("correctAnswer").ifBlank { obj.optString("answer") }.trim()
            val diffRaw = obj.optString("difficulty", "medium")
            val explanation = obj.optString("explanation").trim()
            val incArray = obj.optJSONArray("incorrectAnswers") ?: obj.optJSONArray("options") ?: JSONArray()
            val distractors = (0 until incArray.length())
                .map { incArray.optString(it).trim() }
                .filter { it.isNotBlank() && !it.equals(correct, ignoreCase = true) }
                .distinct()
                .take(3)

            if (qText.isBlank() || correct.isBlank() || distractors.size < 2) continue
            val options = (distractors + correct).distinct().shuffled(rng)

            val resolvedExplanation = explanation.ifBlank {
                val sentences = article.extract
                    .replace(Regex("\\s+"), " ")
                    .split(Regex("(?<=[.!?])\\s+"))
                    .filter { it.length >= 25 }
                sentences.firstOrNull { it.contains(correct, ignoreCase = true) }
                    ?: sentences.firstOrNull()
                    ?: correct
            }

            parsed.add(
                TriviaQuestion(
                    id = "wiki_${article.wikiLang}_${article.pageId}_$i",
                    category = TriviaCategory.WIKIPEDIA,
                    difficulty = QuizDifficulty.fromKey(diffRaw).let {
                        if (it == QuizDifficulty.ALL) QuizDifficulty.MEDIUM else it
                    },
                    questionText = qText,
                    correctAnswer = correct,
                    allOptions = options,
                    pictureQuestion = false,
                    imageDrawableRes = null,
                    imageUrl = article.thumbnailUrl,
                    illustrationTag = article.title,
                    explanation = resolvedExplanation
                )
            )
        }
        return parsed
    }

    private fun readCachedCarousel(
        userScope: String,
        wikiLang: String,
        dateFilter: String?
    ): List<WikipediaArticle>? {
        val raw = themePreferences.getDailyWikiCacheJson(userScope, wikiLang) ?: return null
        return try {
            val root = JSONObject(raw)
            val cachedDate = root.optString("dateKey")
            if (dateFilter != null && cachedDate != dateFilter) return null
            val arr = root.getJSONArray("articles")
            (0 until arr.length()).map { idx ->
                val item = arr.getJSONObject(idx)
                WikipediaArticle(
                    pageId = item.getLong("pageId"),
                    title = item.getString("title"),
                    description = item.optString("description").takeIf { it.isNotBlank() },
                    extract = item.getString("extract"),
                    thumbnailUrl = item.optString("thumbnailUrl").takeIf { it.isNotBlank() },
                    articleUrl = item.optString("articleUrl").takeIf { it.isNotBlank() },
                    wikiLang = item.optString("wikiLang", wikiLang),
                    dateKey = cachedDate
                )
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun saveCachedCarousel(
        userScope: String,
        wikiLang: String,
        dateKey: String,
        articles: List<WikipediaArticle>
    ) {
        val arr = JSONArray()
        articles.forEach { a ->
            arr.put(
                JSONObject().apply {
                    put("pageId", a.pageId)
                    put("title", a.title)
                    put("description", a.description.orEmpty())
                    put("extract", a.extract)
                    put("thumbnailUrl", a.thumbnailUrl.orEmpty())
                    put("articleUrl", a.articleUrl.orEmpty())
                    put("wikiLang", a.wikiLang)
                }
            )
        }
        val payload = JSONObject().apply {
            put("dateKey", dateKey)
            put("articles", arr)
        }
        themePreferences.saveDailyWikiCacheJson(userScope, wikiLang, payload.toString())
    }

    private fun articleKey(wikiLang: String, pageId: Long): String = "${wikiLang}_$pageId"

    companion object {
        private const val TAG = "WikipediaRepository"
        private const val TARGET_CAROUSEL_COUNT = 5

        const val SOURCE_AUTO = "auto"
        const val SOURCE_GEMINI_KEY = "gemini_key"
        const val SOURCE_IMPORTED = "imported"
        const val SOURCE_LOCAL = "local"

        private val modelsMoshiAdapter by lazy {
            Moshi.Builder().build().adapter(GeminiModelsListResponseDto::class.java)
        }

        internal fun parseAvailableModelsJson(rawJson: String): List<GeminiAvailableModel> {
            val trimmed = rawJson.trim()
            if (trimmed.isBlank()) return emptyList()
            val dto = runCatching { modelsMoshiAdapter.fromJson(trimmed) }.getOrNull() ?: return emptyList()
            return dto.models.mapNotNull { item ->
                if (!item.supportedGenerationMethods.contains("generateContent")) return@mapNotNull null
                val modelId = item.name.trim().removePrefix("models/").trim()
                if (modelId.isBlank()) return@mapNotNull null
                val displayName = item.displayName.trim().ifBlank { modelId }
                GeminiAvailableModel(
                    modelId = modelId,
                    displayName = displayName,
                    description = item.description.trim()
                )
            }
        }

        internal fun normalizeWebUrl(raw: String): String? {
            val trimmed = raw.trim()
            if (trimmed.isBlank() || trimmed.contains(" ")) return null
            val withScheme = if (trimmed.startsWith("http://", ignoreCase = true) ||
                trimmed.startsWith("https://", ignoreCase = true)
            ) {
                trimmed
            } else {
                "https://$trimmed"
            }
            val parsed = withScheme.toHttpUrlOrNull() ?: return null
            if (!parsed.host.contains('.')) return null
            return parsed.toString()
        }

        internal fun extractWikipediaUrlTarget(url: String): Pair<String, String>? {
            val parsed = url.toHttpUrlOrNull() ?: return null
            val hostMatch = Regex("^([a-z]{2,3})(?:\\.m)?\\.wikipedia\\.org$", RegexOption.IGNORE_CASE)
                .matchEntire(parsed.host) ?: return null
            val subdomain = hostMatch.groupValues[1].lowercase(Locale.US)
            val segments = parsed.pathSegments
            val wikiIdx = segments.indexOf("wiki")
            if (wikiIdx < 0 || wikiIdx + 1 >= segments.size) return null
            val rawTitleSegment = segments.subList(wikiIdx + 1, segments.size).joinToString("/")
            val decoded = runCatching {
                URLDecoder.decode(rawTitleSegment, "UTF-8")
            }.getOrDefault(rawTitleSegment)
                .replace('_', ' ')
                .trim()
            if (decoded.isBlank()) return null
            return subdomain to decoded
        }

        internal fun extractReadableWebArticle(
            html: String,
            sourceUrl: String,
            languageCode: String,
            dateKey: String = "custom"
        ): WikipediaArticle? {
            if (html.isBlank()) return null

            val cleanMetaDesc = extractMetaContent(html, "property", "og:description")
                ?: extractMetaContent(html, "name", "description")
            val ogTitle = extractMetaContent(html, "property", "og:title")
            val tagTitle = Regex("(?is)<title[^>]*>(.*?)</title>")
                .find(html)?.groupValues?.getOrNull(1)
                ?.let { stripTagsAndDecode(it) }
            val h1Title = Regex("(?is)<h1[^>]*>(.*?)</h1>")
                .find(html)?.groupValues?.getOrNull(1)
                ?.let { stripTagsAndDecode(it) }

            val resolvedTitle = listOfNotNull(ogTitle, h1Title, tagTitle)
                .map { it.trim() }
                .firstOrNull { it.isNotBlank() }
                ?: sourceUrl.toHttpUrlOrNull()?.host
                ?: "Web Article"

            val rawThumb = extractMetaContent(html, "property", "og:image")
                ?.trim()
                ?.takeIf { url ->
                    (url.startsWith("http://") || url.startsWith("https://")) &&
                        !url.endsWith(".svg", ignoreCase = true)
                }

            val strippedHtml = html
                .replace(Regex("(?is)<!--.*?-->"), " ")
                .replace(Regex("(?is)<(script|style|noscript|svg|nav|footer|header|aside|form)[^>]*>.*?</\\1>"), " ")

            val articleContainer = Regex("(?is)<article[^>]*>(.*?)</article>").find(strippedHtml)?.groupValues?.getOrNull(1)
            val mainContainer = Regex("(?is)<main[^>]*>(.*?)</main>").find(strippedHtml)?.groupValues?.getOrNull(1)
            val bodyContainer = Regex("(?is)<body[^>]*>(.*?)</body>").find(strippedHtml)?.groupValues?.getOrNull(1)
                ?: strippedHtml

            fun extractBlocksFromHtml(fragment: String): List<String> {
                val blockRegex = Regex("(?is)<(h[1-4]|p|blockquote|li)[^>]*>(.*?)</\\1>")
                val blocks = ArrayList<String>()
                blockRegex.findAll(fragment).forEach { match ->
                    val tag = match.groupValues[1].lowercase(Locale.US)
                    val innerText = stripTagsAndDecode(match.groupValues[2])
                    if (tag.startsWith("h")) {
                        if (innerText.length in 3..140 && !innerText.equals(resolvedTitle, ignoreCase = true)) {
                            blocks.add("== $innerText ==")
                        }
                    } else if (innerText.length >= 25) {
                        blocks.add(innerText)
                    }
                }
                return blocks
            }

            val selectedBlocks = listOfNotNull(articleContainer, mainContainer, bodyContainer)
                .map { extractBlocksFromHtml(it) }
                .firstOrNull { list -> list.sumOf { it.length } >= 180 }
                ?: extractBlocksFromHtml(bodyContainer)

            val combinedExtract = selectedBlocks.joinToString("\n\n").trim().ifBlank {
                listOfNotNull(cleanMetaDesc, stripTagsAndDecode(bodyContainer))
                    .joinToString("\n\n")
                    .trim()
            }

            if (combinedExtract.length < 60) return null

            val hostLang = sourceUrl.toHttpUrlOrNull()?.host?.removePrefix("www.")?.take(18)
                ?: WikipediaLanguageMapper.toWikipediaSubdomain(languageCode)
            val syntheticPageId = -(abs(sourceUrl.hashCode().toLong()).coerceAtLeast(1L))

            return WikipediaArticle(
                pageId = syntheticPageId,
                title = resolvedTitle.take(160),
                description = cleanMetaDesc?.take(220) ?: hostLang,
                extract = combinedExtract.take(18_000),
                thumbnailUrl = rawThumb,
                articleUrl = sourceUrl,
                wikiLang = WikipediaLanguageMapper.toWikipediaSubdomain(languageCode),
                dateKey = dateKey
            )
        }

        private fun extractMetaContent(html: String, attrName: String, attrValue: String): String? {
            val pattern1 = Regex(
                "(?is)<meta[^>]*\\b$attrName\\s*=\\s*[\"']$attrValue[\"'][^>]*\\bcontent\\s*=\\s*[\"'](.*?)[\"'][^>]*>"
            )
            val pattern2 = Regex(
                "(?is)<meta[^>]*\\bcontent\\s*=\\s*[\"'](.*?)[\"'][^>]*\\b$attrName\\s*=\\s*[\"']$attrValue[\"'][^>]*>"
            )
            val raw = pattern1.find(html)?.groupValues?.getOrNull(1)
                ?: pattern2.find(html)?.groupValues?.getOrNull(1)
                ?: return null
            return stripTagsAndDecode(raw).takeIf { it.isNotBlank() }
        }

        private fun stripTagsAndDecode(raw: String): String {
            val noTags = raw.replace(Regex("<[^>]+>"), " ")
            return OnlineTriviaSources.decodeHtml(noTags)
                .replace(Regex("\\s+"), " ")
                .trim()
        }
    }
}
