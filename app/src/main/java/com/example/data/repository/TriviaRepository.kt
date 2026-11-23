package com.example.data.repository

import android.util.Log
import com.example.data.firebase.UserProgressRepository
import com.example.data.local.CachedQuestionEntity
import com.example.data.local.CategoryCacheCount
import com.example.data.local.GuestSessionEntity
import com.example.data.local.LocalizedSeedRegistry
import com.example.data.local.OfflineTriviaSeed
import com.example.data.local.PendingScoreEntity
import com.example.data.local.ThemePreferences
import com.example.data.local.TriviaDao
import com.example.data.model.GuestStatsSummary
import com.example.data.model.QuizDifficulty
import com.example.data.model.QuizMode
import com.example.data.model.QuizRoundResult
import com.example.data.model.QuizSession
import com.example.data.model.TriviaApiQuestionDto
import com.example.data.model.TriviaCategory
import com.example.data.model.TriviaQuestion
import com.example.data.remote.NetworkMonitor
import com.example.data.remote.OnlineTriviaSources
import com.example.data.remote.TriviaApiService
import com.google.firebase.Timestamp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

data class SessionQuestionBundle(
    val questions: List<TriviaQuestion>,
    val servedFromOfflineCache: Boolean,
    val errorCode: String? = null
)

class TriviaRepository(
    private val apiService: TriviaApiService,
    private val dao: TriviaDao,
    private val networkMonitor: NetworkMonitor,
    private val themePreferences: ThemePreferences,
    private val onlineSources: OnlineTriviaSources = OnlineTriviaSources()
) {
    val totalCachedCount: Flow<Int> = dao.observeTotalCachedCount()
    val pictureQuestionCount: Flow<Int> = dao.observePictureQuestionCount()
    val categoryCacheCounts: Flow<List<CategoryCacheCount>> = dao.observeCategoryCounts()

    @Volatile
    private var seededThisProcess = false

    private val lastServedIdsByBucket = ConcurrentHashMap<String, Set<String>>()

    fun observePendingScoreCount(userId: String): Flow<Int> = dao.observePendingScoreCount(userId)

    fun observeGuestStats(): Flow<GuestStatsSummary> = dao.observeAllGuestSessions().map { list ->
        if (list.isEmpty()) {
            GuestStatsSummary()
        } else {
            val totalScore = list.sumOf { it.score }
            val highScore = list.maxOf { it.score }
            val gamesPlayed = list.size.toLong()
            val totalCorrect = list.sumOf { it.correctCount.toLong() }
            val totalAnswered = list.sumOf { it.totalQuestions.toLong() }
            val bestStreak = list.maxOf { it.bestStreak.toLong() }
            GuestStatsSummary(
                totalScore = totalScore,
                highScore = highScore,
                gamesPlayed = gamesPlayed,
                totalCorrect = totalCorrect,
                totalAnswered = totalAnswered,
                bestStreak = bestStreak
            )
        }
    }

    fun observeGuestSessions(): Flow<List<QuizSession>> = dao.observeAllGuestSessions().map { list ->
        list.take(25).map { entity ->
            QuizSession(
                id = "guest_${entity.id}",
                category = entity.category,
                difficulty = entity.difficulty,
                mode = entity.mode,
                score = entity.score,
                correctCount = entity.correctCount.toLong(),
                totalQuestions = entity.totalQuestions.toLong(),
                bestStreak = entity.bestStreak.toLong(),
                durationSeconds = entity.durationSeconds.toLong(),
                playedOffline = entity.playedOffline,
                createdAt = Timestamp(Date(entity.createdAtMillis))
            )
        }
    }

    suspend fun saveGuestSession(result: QuizRoundResult) = withContext(Dispatchers.IO) {
        dao.insertGuestSession(
            GuestSessionEntity(
                category = result.category.apiKey,
                difficulty = result.difficulty.apiKey,
                mode = result.mode.key,
                score = result.score,
                correctCount = result.correctCount,
                totalQuestions = result.totalQuestions,
                bestStreak = result.bestStreak,
                durationSeconds = result.durationSeconds,
                playedOffline = result.playedOffline
            )
        )
    }

    suspend fun ensureSeeded() = withContext(Dispatchers.IO) {
        val cleared = themePreferences.libraryCleared.first()
        if (cleared) return@withContext
        if (!seededThisProcess) {
            dao.insertQuestionsIgnore(OfflineTriviaSeed.initialSeedQuestions())
            seededThisProcess = true
        }
    }

    suspend fun deleteAllLibrary() = withContext(Dispatchers.IO) {
        themePreferences.setLibraryCleared(true)
        dao.deleteAllCachedQuestions()
        seededThisProcess = false
        lastServedIdsByBucket.clear()
    }

    suspend fun refreshLibrary(): Result<Int> = withContext(Dispatchers.IO) {
        val online = networkMonitor.checkCurrentConnectivity()
        themePreferences.setLibraryCleared(false)
        dao.deleteApiCachedQuestions()
        dao.insertQuestionsIgnore(OfflineTriviaSeed.initialSeedQuestions())
        seededThisProcess = true
        lastServedIdsByBucket.clear()

        if (!online) {
            return@withContext Result.success(dao.getTotalCount())
        }

        return@withContext try {
            val triviaApiBatch = runCatching {
                apiService.getQuestions(limit = 50, types = "text_choice").mapNotNull { it.toEntityOrNull() }
            }.getOrDefault(emptyList())
            val openTdbBatch = onlineSources.fetchOpenTdbQuestions(
                category = TriviaCategory.MIXED,
                difficulty = QuizDifficulty.ALL,
                amount = 25
            )
            val combined = (triviaApiBatch + openTdbBatch).distinctBy { it.id }
            if (combined.isNotEmpty()) {
                val rng = Random(System.currentTimeMillis())
                val domainList = combined.map { it.toDomain("en", rng) }
                val enriched = onlineSources.enrichExplanationsWithWikipedia(domainList, "en")
                    .associate { it.id to it.explanation }
                val enrichedEntities = combined.map { entity ->
                    entity.copy(explanation = enriched[entity.id]?.takeIf { it.isNotBlank() } ?: entity.explanation)
                }
                dao.insertQuestionsIgnore(enrichedEntities)
            }
            Result.success(dao.getTotalCount())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchSessionQuestions(
        category: TriviaCategory,
        difficulty: QuizDifficulty,
        mode: QuizMode,
        questionCount: Int,
        languageCode: String = "en"
    ): SessionQuestionBundle = withContext(Dispatchers.IO) {
        val offlineModeEnabled = themePreferences.offlineModeEnabled.first()
        if (offlineModeEnabled) {
            ensureSeeded()
        }

        val clampedCount = questionCount.coerceIn(3, 20)
        val normalizedLang = when (languageCode.lowercase()) {
            "es" -> "es"
            "in", "id" -> "in"
            else -> "en"
        }
        val preferLocalized = normalizedLang == "es" || normalizedLang == "in"
        val rng = Random(System.nanoTime() xor System.currentTimeMillis())
        val isOnline = networkMonitor.checkCurrentConnectivity()

        if (!offlineModeEnabled && !isOnline) {
            return@withContext SessionQuestionBundle(
                questions = emptyList(),
                servedFromOfflineCache = false,
                errorCode = "OFFLINE_DISABLED"
            )
        }

        // 1. Picture Atlas / Visual Mode: Live Wikipedia illustrated questions across EN / ES / ID
        if (mode == QuizMode.PICTURE || category == TriviaCategory.PICTURE_ATLAS) {
            if (isOnline) {
                val picBucket = "picture_live_$normalizedLang"
                val livePictures = try {
                    onlineSources.fetchLiveWikipediaPictureQuestions(
                        languageCode = normalizedLang,
                        mode = mode,
                        count = clampedCount,
                        excludeIds = lastServedIdsByBucket[picBucket].orEmpty(),
                        rng = rng
                    )
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    runCatching { Log.w(TAG, "Live Wikipedia picture fetch failed", e) }
                    emptyList()
                }

                if (livePictures.isNotEmpty()) {
                    lastServedIdsByBucket[picBucket] = livePictures.mapTo(mutableSetOf()) { it.id }
                    return@withContext SessionQuestionBundle(
                        questions = livePictures,
                        servedFromOfflineCache = false
                    )
                }

                if (!offlineModeEnabled) {
                    return@withContext SessionQuestionBundle(
                        questions = emptyList(),
                        servedFromOfflineCache = false,
                        errorCode = "LIVE_FETCH_ERROR"
                    )
                }
            }

            if (!offlineModeEnabled) {
                return@withContext SessionQuestionBundle(emptyList(), false, "OFFLINE_DISABLED")
            }

            // Offline Picture Atlas fallback (strictly respects Delete All: reads Room ONLY)
            val allPictures = dao.getAllPictureQuestions()
            if (allPictures.isEmpty()) {
                return@withContext SessionQuestionBundle(
                    questions = emptyList(),
                    servedFromOfflineCache = true,
                    errorCode = "LIBRARY_EMPTY"
                )
            }
            val candidatePool = if (preferLocalized) {
                val localizedOnly = allPictures.filter {
                    LocalizedSeedRegistry.getLocalizedCopy(it.id, normalizedLang) != null
                }
                if (localizedOnly.size >= clampedCount) localizedOnly else allPictures
            } else {
                allPictures
            }
            val selected = pickRandomizedQuestions(
                candidates = candidatePool,
                count = clampedCount,
                bucketKey = "picture_offline_$normalizedLang",
                difficulty = difficulty,
                rng = rng
            )
            if (selected.isNotEmpty()) {
                dao.markQuestionsServed(selected.map { it.id }, System.currentTimeMillis())
            }
            return@withContext SessionQuestionBundle(
                questions = selected.map { it.toDomain(normalizedLang, rng) },
                servedFromOfflineCache = true
            )
        }

        val liveBucketKey = "live_${category.apiKey}_$normalizedLang"
        val offlineBucketKey = "offline_${category.apiKey}_$normalizedLang"

        // 2. Standard / Guess Mode Online-First (EN, ES, ID)
        if (isOnline) {
            try {
                val liveCandidates = fetchLiveEnglishBatch(
                    category = category,
                    difficulty = difficulty,
                    clampedCount = clampedCount,
                    mode = mode
                )

                if (liveCandidates.isNotEmpty()) {
                    val selectedEntities = pickRandomizedQuestions(
                        candidates = liveCandidates,
                        count = clampedCount,
                        bucketKey = liveBucketKey,
                        difficulty = difficulty,
                        rng = rng
                    )
                    val domainEn = selectedEntities.map { it.toDomain("en", rng) }

                    val targetQuestions = if (preferLocalized) {
                        onlineSources.translateQuestionsOnline(domainEn, normalizedLang, rng)
                    } else {
                        domainEn
                    }

                    if (targetQuestions.isNotEmpty()) {
                        val enriched = onlineSources.enrichExplanationsWithWikipedia(
                            questions = targetQuestions,
                            languageCode = normalizedLang,
                            englishFallbackById = if (preferLocalized) domainEn.associateBy { it.id } else emptyMap()
                        )
                        if (offlineModeEnabled && !preferLocalized) {
                            val explanationById = enriched.associate { it.id to it.explanation }
                            dao.insertQuestionsIgnore(
                                selectedEntities.map { entity ->
                                    entity.copy(
                                        explanation = explanationById[entity.id]?.takeIf { it.isNotBlank() }
                                            ?: entity.explanation
                                    )
                                }
                            )
                        }
                        return@withContext SessionQuestionBundle(
                            questions = enriched,
                            servedFromOfflineCache = false
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                runCatching { Log.w(TAG, "Live trivia session fetch failed", e) }
            }

            // Live failed or translation failed: never fall back silently when Offline Mode is OFF
            if (!offlineModeEnabled) {
                return@withContext SessionQuestionBundle(
                    questions = emptyList(),
                    servedFromOfflineCache = false,
                    errorCode = "LIVE_FETCH_ERROR"
                )
            }
        }

        if (!offlineModeEnabled) {
            return@withContext SessionQuestionBundle(
                questions = emptyList(),
                servedFromOfflineCache = false,
                errorCode = "OFFLINE_DISABLED"
            )
        }

        // 3. Offline Fallback (strictly reads Room ONLY so Delete All stays empty until Refresh)
        val cachedCategoryPool = dao.getAllStandardQuestionsForCategory(category.apiKey)
        val filteredPool = if (preferLocalized) {
            cachedCategoryPool.filter {
                LocalizedSeedRegistry.getLocalizedCopy(it.id, normalizedLang) != null
            }
        } else {
            cachedCategoryPool
        }

        val resultCached = pickRandomizedQuestions(
            candidates = filteredPool,
            count = clampedCount,
            bucketKey = offlineBucketKey,
            difficulty = difficulty,
            rng = rng
        ).toMutableList()

        if (resultCached.size < clampedCount && category != TriviaCategory.MIXED) {
            val alreadyPicked = resultCached.mapTo(mutableSetOf()) { it.id }
            val previousIds = lastServedIdsByBucket[offlineBucketKey].orEmpty()
            val mixedPool = dao.getAllStandardQuestionsForCategory(TriviaCategory.MIXED.apiKey)
                .filter { entity ->
                    entity.id !in alreadyPicked &&
                        (!preferLocalized || LocalizedSeedRegistry.getLocalizedCopy(entity.id, normalizedLang) != null)
                }
            val freshExtra = mixedPool.filter { it.id !in previousIds }.shuffled(rng)
            val fallbackExtra = mixedPool.filter { it.id in previousIds }.shuffled(rng)
            for (item in (freshExtra + fallbackExtra)) {
                if (resultCached.size >= clampedCount) break
                resultCached.add(item)
            }
        }

        val finalCached = resultCached.shuffled(rng)
        if (finalCached.isEmpty()) {
            return@withContext SessionQuestionBundle(
                questions = emptyList(),
                servedFromOfflineCache = true,
                errorCode = "LIBRARY_EMPTY"
            )
        }

        lastServedIdsByBucket[offlineBucketKey] = finalCached.mapTo(mutableSetOf()) { it.id }
        dao.markQuestionsServed(finalCached.map { it.id }, System.currentTimeMillis())

        val domainOffline = finalCached.map { it.toDomain(normalizedLang, rng) }
        val finalOffline = if (isOnline) {
            onlineSources.enrichExplanationsWithWikipedia(domainOffline, normalizedLang)
        } else {
            domainOffline
        }
        SessionQuestionBundle(
            questions = finalOffline,
            servedFromOfflineCache = true
        )
    }

    private suspend fun fetchLiveEnglishBatch(
        category: TriviaCategory,
        difficulty: QuizDifficulty,
        clampedCount: Int,
        mode: QuizMode
    ): List<CachedQuestionEntity> {
        val difficultyParam = if (difficulty == QuizDifficulty.ALL) null else difficulty.apiKey
        val triviaLimit = maxOf(clampedCount * 3, 25).coerceIn(1, 50)

        if (category == TriviaCategory.TECHNOLOGY) {
            val computersBatch = onlineSources.fetchOpenTdbQuestions(
                category = TriviaCategory.TECHNOLOGY,
                difficulty = difficulty,
                amount = maxOf(clampedCount * 2, 18).coerceIn(5, 30),
                explicitCategoryId = 18
            )
            val gadgetsBatch = onlineSources.fetchOpenTdbQuestions(
                category = TriviaCategory.TECHNOLOGY,
                difficulty = difficulty,
                amount = maxOf(clampedCount, 12).coerceIn(5, 25),
                explicitCategoryId = 30
            )
            val combinedTech = (computersBatch + gadgetsBatch).distinctBy { it.questionText.trim().lowercase() }
            if (mode == QuizMode.GUESS && combinedTech.size > clampedCount) {
                val concise = combinedTech.filter { it.correctAnswer.length <= 36 }
                if (concise.size >= clampedCount) return concise
            }
            return combinedTech
        }

        val categoryParam = if (category == TriviaCategory.MIXED) null else category.apiKey

        val primaryList = runCatching {
            apiService.getQuestions(
                limit = triviaLimit,
                categories = categoryParam,
                difficulties = difficultyParam,
                types = "text_choice"
            ).mapNotNull { it.toEntityOrNull() }
        }.onFailure { err ->
            if (err is CancellationException) throw err
            runCatching { Log.w(TAG, "The Trivia API live request failed", err) }
        }.getOrDefault(emptyList())

        val secondaryList = if (primaryList.size < clampedCount * 2) {
            onlineSources.fetchOpenTdbQuestions(
                category = category,
                difficulty = difficulty,
                amount = maxOf(clampedCount * 2, 20).coerceIn(5, 30)
            )
        } else {
            emptyList()
        }

        val merged = (primaryList + secondaryList).distinctBy { it.questionText.trim().lowercase() }
        if (mode == QuizMode.GUESS && merged.size > clampedCount) {
            val concise = merged.filter { it.correctAnswer.length <= 36 }
            if (concise.size >= clampedCount) return concise
        }
        return merged
    }

    /**
     * Fetches a shared, balanced set of battle questions online across host-selected topics
     * in EN / ES / ID, enriching all answers with concise Wikipedia explanations.
     */
    suspend fun fetchBattleQuestions(
        topics: List<TriviaCategory>,
        difficulty: QuizDifficulty,
        mode: QuizMode,
        count: Int,
        languageCode: String = "en"
    ): List<TriviaQuestion> = withContext(Dispatchers.IO) {
        val safeCount = count.coerceIn(5, 20)
        val normalizedLang = when (languageCode.lowercase()) {
            "es" -> "es"
            "in", "id" -> "in"
            else -> "en"
        }
        val preferLocalized = normalizedLang == "es" || normalizedLang == "in"
        val rng = Random(System.nanoTime() xor System.currentTimeMillis())
        val isOnline = networkMonitor.checkCurrentConnectivity()

        val cleanTopics = topics
            .filter { it != TriviaCategory.WIKIPEDIA }
            .distinct()
            .ifEmpty { listOf(TriviaCategory.GENERAL_KNOWLEDGE) }

        val seedBank = OfflineTriviaSeed.initialSeedQuestions()

        // Picture battle or explicit Picture Atlas topic
        if (mode == QuizMode.PICTURE || (cleanTopics.size == 1 && cleanTopics.first() == TriviaCategory.PICTURE_ATLAS)) {
            if (isOnline) {
                val livePictures = runCatching {
                    onlineSources.fetchLiveWikipediaPictureQuestions(
                        languageCode = normalizedLang,
                        mode = mode,
                        count = safeCount,
                        excludeIds = emptySet(),
                        rng = rng
                    )
                }.onFailure { if (it is CancellationException) throw it }.getOrDefault(emptyList())
                if (livePictures.isNotEmpty()) {
                    return@withContext livePictures
                }
            }

            val allPictures = (dao.getAllPictureQuestions() + seedBank.filter { it.pictureQuestion })
                .distinctBy { it.id }
            val pool = if (preferLocalized) {
                val localized = allPictures.filter {
                    LocalizedSeedRegistry.getLocalizedCopy(it.id, normalizedLang) != null
                }
                if (localized.size >= safeCount) localized else allPictures
            } else {
                allPictures
            }
            return@withContext pickRandomizedQuestions(
                candidates = pool,
                count = safeCount,
                bucketKey = "battle_pic_$normalizedLang",
                difficulty = difficulty,
                rng = rng
            ).map { it.toDomain(normalizedLang, rng) }
        }

        val effectiveTopics = cleanTopics
            .filter { it != TriviaCategory.PICTURE_ATLAS }
            .ifEmpty { listOf(TriviaCategory.GENERAL_KNOWLEDGE) }

        val collectedOnline = ArrayList<CachedQuestionEntity>(safeCount)
        val usedOnlineIds = HashSet<String>()

        fun addUnique(
            targetList: MutableList<CachedQuestionEntity>,
            seenSet: MutableSet<String>,
            candidates: List<CachedQuestionEntity>,
            targetAdd: Int
        ) {
            var added = 0
            val filteredDiff = if (difficulty == QuizDifficulty.ALL) {
                candidates.shuffled(rng)
            } else {
                candidates.filter { it.difficulty.equals(difficulty.apiKey, ignoreCase = true) }.shuffled(rng) +
                    candidates.filterNot { it.difficulty.equals(difficulty.apiKey, ignoreCase = true) }.shuffled(rng)
            }
            for (item in filteredDiff) {
                if (added >= targetAdd || targetList.size >= safeCount) break
                if (seenSet.add(item.id)) {
                    targetList.add(item)
                    added++
                }
            }
        }

        val basePerTopic = safeCount / effectiveTopics.size
        val remainder = safeCount % effectiveTopics.size

        if (isOnline) {
            effectiveTopics.forEachIndexed { idx, topic ->
                val quota = (basePerTopic + if (idx < remainder) 1 else 0).coerceAtLeast(1)
                val liveTopicPool = runCatching {
                    fetchLiveEnglishBatch(
                        category = topic,
                        difficulty = difficulty,
                        clampedCount = quota,
                        mode = mode
                    )
                }.onFailure { if (it is CancellationException) throw it }.getOrDefault(emptyList())
                addUnique(collectedOnline, usedOnlineIds, liveTopicPool, quota)
            }

            if (collectedOnline.isNotEmpty()) {
                val liveDomainEn = collectedOnline
                    .distinctBy { it.questionText.trim().lowercase() }
                    .shuffled(rng)
                    .take(safeCount)
                    .map { it.toDomain("en", rng) }

                val localizedLive = if (preferLocalized) {
                    onlineSources.translateQuestionsOnline(liveDomainEn, normalizedLang, rng)
                } else {
                    liveDomainEn
                }
                if (localizedLive.size >= minOf(safeCount, 3)) {
                    return@withContext onlineSources.enrichExplanationsWithWikipedia(
                        questions = localizedLive,
                        languageCode = normalizedLang,
                        englishFallbackById = if (preferLocalized) liveDomainEn.associateBy { it.id } else emptyMap()
                    )
                }
            }
        }

        // Fallback if offline or translation unavailable
        val collectedFallback = ArrayList<CachedQuestionEntity>(safeCount)
        val usedFallbackIds = HashSet<String>()
        val poolSource = (dao.getAllStandardQuestionsForCategory(TriviaCategory.MIXED.apiKey) +
            seedBank.filter { !it.pictureQuestion }).distinctBy { it.id }

        val finalPool = if (preferLocalized) {
            poolSource.filter { LocalizedSeedRegistry.getLocalizedCopy(it.id, normalizedLang) != null }
        } else {
            poolSource
        }

        effectiveTopics.forEachIndexed { idx, topic ->
            val quota = basePerTopic + if (idx < remainder) 1 else 0
            if (quota <= 0) return@forEachIndexed
            val topicLocal = if (topic == TriviaCategory.MIXED) {
                finalPool
            } else {
                finalPool.filter { it.category == topic.apiKey }
            }
            addUnique(collectedFallback, usedFallbackIds, topicLocal, quota)
        }
        if (collectedFallback.size < safeCount) {
            addUnique(collectedFallback, usedFallbackIds, finalPool, safeCount - collectedFallback.size)
        }

        val domainFallback = collectedFallback
            .distinctBy { it.questionText.trim().lowercase() }
            .shuffled(rng)
            .take(safeCount)
            .map { it.toDomain(normalizedLang, rng) }

        return@withContext if (isOnline) {
            onlineSources.enrichExplanationsWithWikipedia(domainFallback, normalizedLang)
        } else {
            domainFallback
        }
    }

    internal fun pickRandomizedQuestions(
        candidates: List<CachedQuestionEntity>,
        count: Int,
        bucketKey: String,
        difficulty: QuizDifficulty,
        rng: Random
    ): List<CachedQuestionEntity> {
        val unique = candidates.distinctBy { it.id }
        if (unique.isEmpty() || count <= 0) return emptyList()

        val previousIds = lastServedIdsByBucket[bucketKey].orEmpty()
        val freshTier = unique.filter { it.id !in previousIds }
        val repeatTier = unique.filter { it.id in previousIds }

        fun orderTier(tier: List<CachedQuestionEntity>): List<CachedQuestionEntity> {
            if (difficulty == QuizDifficulty.ALL) return tier.shuffled(rng)
            val matching = tier.filter { it.difficulty.equals(difficulty.apiKey, ignoreCase = true) }.shuffled(rng)
            val other = tier.filterNot { it.difficulty.equals(difficulty.apiKey, ignoreCase = true) }.shuffled(rng)
            return matching + other
        }

        val combined = (orderTier(freshTier) + orderTier(repeatTier))
            .take(count)
            .shuffled(rng)

        lastServedIdsByBucket[bucketKey] = combined.mapTo(mutableSetOf()) { it.id }
        return combined
    }

    suspend fun syncOfflinePack(): Result<Int> = withContext(Dispatchers.IO) {
        val before = dao.getTotalCount()
        refreshLibrary().map { after -> (after - before).coerceAtLeast(0) }
    }

    suspend fun queueOfflineScore(userId: String, result: QuizRoundResult) = withContext(Dispatchers.IO) {
        dao.insertPendingScore(
            PendingScoreEntity(
                userId = userId,
                category = result.category.apiKey,
                difficulty = result.difficulty.apiKey,
                mode = result.mode.key,
                score = result.score,
                correctCount = result.correctCount,
                totalQuestions = result.totalQuestions,
                bestStreak = result.bestStreak,
                durationSeconds = result.durationSeconds
            )
        )
    }

    suspend fun flushPendingScores(
        userId: String,
        progressRepo: UserProgressRepository,
        fallbackDisplayName: String?
    ): Int = withContext(Dispatchers.IO) {
        if (!networkMonitor.checkCurrentConnectivity()) return@withContext 0
        val pending = dao.getPendingScoresForUser(userId)
        var syncedCount = 0
        for (item in pending) {
            val roundResult = QuizRoundResult(
                category = TriviaCategory.fromApiKey(item.category),
                difficulty = QuizDifficulty.fromKey(item.difficulty),
                mode = QuizMode.fromKey(item.mode),
                score = item.score,
                correctCount = item.correctCount,
                totalQuestions = item.totalQuestions,
                bestStreak = item.bestStreak,
                durationSeconds = item.durationSeconds,
                playedOffline = true
            )
            val res = progressRepo.recordQuizResult(userId, roundResult, fallbackDisplayName)
            if (res.isSuccess) {
                dao.deletePendingScore(item.id)
                syncedCount++
            } else {
                break
            }
        }
        syncedCount
    }

    private fun TriviaApiQuestionDto.toEntityOrNull(): CachedQuestionEntity? {
        val text = OnlineTriviaSources.decodeHtml(question?.text?.trim().orEmpty())
        val answer = OnlineTriviaSources.decodeHtml(correctAnswer.trim())
        val distractors = incorrectAnswers
            .map { OnlineTriviaSources.decodeHtml(it).trim() }
            .filter { it.isNotBlank() && !it.equals(answer, ignoreCase = true) }
            .distinct()
        if (text.isBlank() || answer.isBlank() || distractors.isEmpty()) return null
        val remoteImg = imageUrl ?: question?.image
        val isPicture = type == "image_choice" && !remoteImg.isNullOrBlank()
        val domainCategory = TriviaCategory.fromApiKey(category)
        val domainDifficulty = QuizDifficulty.fromKey(difficulty).let {
            if (it == QuizDifficulty.ALL) QuizDifficulty.MEDIUM else it
        }

        return CachedQuestionEntity(
            id = "api_$id",
            category = domainCategory.apiKey,
            difficulty = domainDifficulty.apiKey,
            questionText = text,
            correctAnswer = answer,
            incorrectAnswersSerialized = OfflineTriviaSeed.encodeIncorrect(distractors),
            pictureQuestion = isPicture,
            imageUrl = remoteImg,
            explanation = explanation?.trim().orEmpty(),
            cachedAt = System.currentTimeMillis()
        )
    }

    private fun CachedQuestionEntity.toDomain(languageCode: String, rng: Random): TriviaQuestion {
        val localized = LocalizedSeedRegistry.getLocalizedCopy(id, languageCode)
        val finalQuestionText = localized?.questionText ?: questionText
        val finalCorrectAnswer = localized?.correctAnswer ?: correctAnswer
        val incorrectList = localized?.incorrectAnswers
            ?: OfflineTriviaSeed.decodeIncorrect(incorrectAnswersSerialized)
        val options = (incorrectList + finalCorrectAnswer)
            .distinct()
            .shuffled(rng)
        val domainCat = TriviaCategory.fromApiKey(category)
        val domainDiff = QuizDifficulty.fromKey(difficulty)
        val resolvedExplanation = localized?.explanation?.takeIf { it.isNotBlank() }
            ?: explanation?.takeIf { it.isNotBlank() }
            ?: OnlineTriviaSources.buildContextualFallbackExplanation(
                questionText = finalQuestionText,
                answer = finalCorrectAnswer,
                category = domainCat,
                languageCode = languageCode
            )

        return TriviaQuestion(
            id = id,
            category = domainCat,
            difficulty = domainDiff,
            questionText = finalQuestionText,
            correctAnswer = finalCorrectAnswer,
            allOptions = options,
            pictureQuestion = pictureQuestion,
            imageDrawableRes = OfflineTriviaSeed.drawableResByName(imageDrawableName),
            imageUrl = imageUrl,
            illustrationTag = illustrationTag,
            explanation = resolvedExplanation
        )
    }

    private companion object {
        const val TAG = "TriviaRepository"
    }
}
