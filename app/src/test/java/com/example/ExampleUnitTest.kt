package com.example

import com.example.data.local.LocalGeminiKeyVault
import com.example.data.local.LocalizedSeedRegistry
import com.example.data.local.OfflineTriviaSeed
import com.example.data.model.AnswerMatcher
import com.example.data.model.AppLanguage
import com.example.data.model.DailyObjectiveScenario
import com.example.data.model.DailyObjectives
import com.example.data.model.ObjectiveLaunchSpec
import com.example.data.model.ProgressionSystem
import com.example.data.model.QuizDifficulty
import com.example.data.model.QuizMode
import com.example.data.model.QuizRoundResult
import com.example.data.model.TriviaCategory
import com.example.data.model.WikipediaLanguageMapper
import com.example.data.remote.OnlineTriviaSources
import com.example.data.repository.WikipediaRepository
import com.example.ui.viewmodel.BattleScoring
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class ExampleUnitTest {
    @Test
    fun answerMatcher_handlesNormalizationArticlesAndAccents() {
        assertTrue(AnswerMatcher.isGuessMatch("the taj mahal!", "Taj Mahal"))
        assertTrue(AnswerMatcher.isGuessMatch("curie", "Marie Curie"))
        assertTrue(AnswerMatcher.isGuessMatch("Sao Paulo", "São Paulo"))
        assertTrue(AnswerMatcher.isGuessMatch("photosyntesis", "Photosynthesis"))
        // Spanish articles & accents
        assertTrue(AnswerMatcher.isGuessMatch("partenon", "El Partenón"))
        assertTrue(AnswerMatcher.isGuessMatch("hititas", "Los Hititas"))
        assertTrue(AnswerMatcher.isGuessMatch("kupu-kupu raja", "Kupu-kupu Raja"))
    }

    @Test
    fun answerMatcher_enforcesStrictNumbersAndRejectsWrongWords() {
        assertTrue(AnswerMatcher.isGuessMatch("1969", "1969"))
        assertFalse(AnswerMatcher.isGuessMatch("1968", "1969"))
        assertFalse(AnswerMatcher.isGuessMatch("Mars", "Venus"))
        assertFalse(AnswerMatcher.isGuessMatch("", "Mercury"))
    }

    @Test
    fun answerMatcher_buildsMaskedHintPattern() {
        val hint = AnswerMatcher.buildHintPattern("Taj Mahal")
        assertEquals("T _ _   _ _ _ _ _  (3, 5)", hint)
    }

    @Test
    fun localizedSeeds_coverAllOfflineAndPictureQuestionsForEsAndIn() {
        val seeds = OfflineTriviaSeed.initialSeedQuestions()
        assertTrue("Expected offline bank to be at least 1000+, got ${seeds.size}", seeds.size >= 1000)
        assertEquals(seeds.size, seeds.map { it.id }.distinct().size)
        assertTrue("Expected at least 24 picture questions", seeds.count { it.pictureQuestion } >= 24)
        TriviaCategory.playableGridCategories.forEach { category ->
            val count = seeds.count { !it.pictureQuestion && it.category == category.apiKey }
            assertTrue("Expected at least 50 seeds for ${category.apiKey}, got $count", count >= 50)
        }
        seeds.forEach { entity ->
            assertFalse("Placeholder answer in ${entity.id}", entity.correctAnswer.contains("Verified Answer #"))
            assertFalse("Placeholder explanation in ${entity.id}", (entity.explanation ?: "").contains("Other choices were"))
            val es = LocalizedSeedRegistry.getLocalizedCopy(entity.id, "es")
            val id = LocalizedSeedRegistry.getLocalizedCopy(entity.id, "in")
            assertNotNull("Missing ES translation for ${entity.id}", es)
            assertNotNull("Missing IN translation for ${entity.id}", id)
            assertTrue("Missing ES distractors for ${entity.id}", es!!.incorrectAnswers.isNotEmpty())
            assertTrue("Missing IN distractors for ${entity.id}", id!!.incorrectAnswers.isNotEmpty())
        }
    }

    @Test
    fun onlineTriviaSources_decodesHtmlEntitiesCleanly() {
        assertEquals(
            "Who wrote \"Les Misérables\" & 'Notre-Dame'?",
            OnlineTriviaSources.decodeHtml("Who wrote &quot;Les Mis&eacute;rables&quot; &amp; &#039;Notre-Dame&#039;?")
        )
        assertEquals("38°C — A", OnlineTriviaSources.decodeHtml("38&deg;C &mdash; &#65;"))
    }

    @Test
    fun appLanguage_mapsIndonesianIsoCodesSafely() {
        assertEquals(AppLanguage.INDONESIAN, AppLanguage.fromCode("id"))
        assertEquals(AppLanguage.INDONESIAN, AppLanguage.fromCode("in"))
        assertEquals(AppLanguage.SPANISH, AppLanguage.fromCode("es"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("en"))
        assertEquals(AppLanguage.SYSTEM, AppLanguage.fromCode(null))
    }

    @Test
    fun wikipediaLanguageMapper_mapsSubdomainsAndTargetNames() {
        assertEquals("id", WikipediaLanguageMapper.toWikipediaSubdomain("in"))
        assertEquals("id", WikipediaLanguageMapper.toWikipediaSubdomain("id"))
        assertEquals("es", WikipediaLanguageMapper.toWikipediaSubdomain("es"))
        assertEquals("en", WikipediaLanguageMapper.toWikipediaSubdomain("en"))
        assertEquals(TriviaCategory.WIKIPEDIA, TriviaCategory.fromApiKey("wikipedia"))
        assertFalse(TriviaCategory.playableGridCategories.contains(TriviaCategory.WIKIPEDIA))
    }

    @Test
    fun progressionSystem_calculatesLifetimeDoublingXpLevels() {
        assertEquals(10_000L, ProgressionSystem.xpRequiredForLevel(1))
        assertEquals(20_000L, ProgressionSystem.xpRequiredForLevel(2))
        assertEquals(40_000L, ProgressionSystem.xpRequiredForLevel(3))
        assertEquals(80_000L, ProgressionSystem.xpRequiredForLevel(4))

        val zero = ProgressionSystem.calculateLevelProgress(0L)
        assertEquals(1, zero.level)
        assertEquals(0L, zero.xpIntoCurrentLevel)
        assertEquals(10_000L, zero.xpRequiredForCurrentLevel)

        val almostLv2 = ProgressionSystem.calculateLevelProgress(9_999L)
        assertEquals(1, almostLv2.level)
        assertEquals(9_999L, almostLv2.xpIntoCurrentLevel)
        assertEquals(1L, almostLv2.xpRemainingToNextLevel)

        val exactLv2 = ProgressionSystem.calculateLevelProgress(10_000L)
        assertEquals(2, exactLv2.level)
        assertEquals(0L, exactLv2.xpIntoCurrentLevel)
        assertEquals(20_000L, exactLv2.xpRequiredForCurrentLevel)

        val exactLv3 = ProgressionSystem.calculateLevelProgress(30_000L)
        assertEquals(3, exactLv3.level)
        assertEquals(0L, exactLv3.xpIntoCurrentLevel)
        assertEquals(40_000L, exactLv3.xpRequiredForCurrentLevel)

        val midLv4 = ProgressionSystem.calculateLevelProgress(75_500L)
        assertEquals(4, midLv4.level)
        assertEquals(5_500L, midLv4.xpIntoCurrentLevel)
        assertEquals(80_000L, midLv4.xpRequiredForCurrentLevel)
    }

    @Test
    fun progressionSystem_resetsWeeklyCycleAtSunday12PmGmt() {
        fun gmtMillis(year: Int, monthZeroBased: Int, day: Int, hour: Int, min: Int, sec: Int): Long {
            return Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.US).apply {
                set(year, monthZeroBased, day, hour, min, sec)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }

        // Sunday Nov 22, 2026 at 11:59:59 GMT -> still in the Nov 15 weekly cycle
        val beforeReset = ProgressionSystem.currentWeeklyCycle(gmtMillis(2026, Calendar.NOVEMBER, 22, 11, 59, 59))
        assertEquals("2026-11-15_1200Z", beforeReset.cycleId)
        assertEquals(1_000L, beforeReset.remainingMillis(gmtMillis(2026, Calendar.NOVEMBER, 22, 11, 59, 59)))

        // Sunday Nov 22, 2026 at 12:00:00 GMT -> immediately transitions to Nov 22 weekly cycle
        val atReset = ProgressionSystem.currentWeeklyCycle(gmtMillis(2026, Calendar.NOVEMBER, 22, 12, 0, 0))
        assertEquals("2026-11-22_1200Z", atReset.cycleId)
        assertNotEquals(beforeReset.cycleId, atReset.cycleId)
        assertEquals(ProgressionSystem.WEEK_MILLIS, atReset.remainingMillis(gmtMillis(2026, Calendar.NOVEMBER, 22, 12, 0, 0)))

        // Mid-week Wednesday stays in Nov 22 weekly cycle and is outside H-1 window
        val wednesdayMillis = gmtMillis(2026, Calendar.NOVEMBER, 25, 18, 30, 0)
        val wednesday = ProgressionSystem.currentWeeklyCycle(wednesdayMillis)
        assertEquals("2026-11-22_1200Z", wednesday.cycleId)
        assertFalse(ProgressionSystem.isWithinResetCountdownWindow(wednesday.remainingMillis(wednesdayMillis)))

        // Saturday Nov 28 at 12:00:00 GMT (exact H-1 = 24h left) and Sunday 08:00 GMT (4h left) are visible
        val satNoonMillis = gmtMillis(2026, Calendar.NOVEMBER, 28, 12, 0, 0)
        assertTrue(ProgressionSystem.isWithinResetCountdownWindow(wednesday.remainingMillis(satNoonMillis)))
        val sunMorningMillis = gmtMillis(2026, Calendar.NOVEMBER, 29, 8, 0, 0)
        assertTrue(ProgressionSystem.isWithinResetCountdownWindow(wednesday.remainingMillis(sunMorningMillis)))
        assertFalse(ProgressionSystem.isWithinResetCountdownWindow(ProgressionSystem.WEEK_MILLIS))
    }

    @Test
    fun battleScoring_enforcesPositiveSpeedBonusNegativePenaltyAndZeroNoAnswer() {
        assertEquals(30, BattleScoring.BATTLE_QUESTION_SECONDS)

        // Faster completion earns strictly more +XP
        val fastXp = BattleScoring.calculateXpDelta(
            isCorrect = true,
            hasAnswer = true,
            difficulty = QuizDifficulty.MEDIUM,
            mode = QuizMode.STANDARD,
            hintRevealed = false,
            secondsRemaining = 28,
            maxSeconds = BattleScoring.BATTLE_QUESTION_SECONDS,
            newStreak = 1
        )
        val slowXp = BattleScoring.calculateXpDelta(
            isCorrect = true,
            hasAnswer = true,
            difficulty = QuizDifficulty.MEDIUM,
            mode = QuizMode.STANDARD,
            hintRevealed = false,
            secondsRemaining = 4,
            maxSeconds = BattleScoring.BATTLE_QUESTION_SECONDS,
            newStreak = 1
        )
        assertTrue("Fast answer XP ($fastXp) should be > 0", fastXp > 0L)
        assertTrue("Faster answer XP ($fastXp) must exceed slower ($slowXp)", fastXp > slowXp)

        // Wrong answer yields negative XP
        val wrongXp = BattleScoring.calculateXpDelta(
            isCorrect = false,
            hasAnswer = true,
            difficulty = QuizDifficulty.MEDIUM,
            mode = QuizMode.STANDARD,
            hintRevealed = false,
            secondsRemaining = 20,
            maxSeconds = BattleScoring.BATTLE_QUESTION_SECONDS,
            newStreak = 0
        )
        assertTrue("Wrong answer XP ($wrongXp) must be negative", wrongXp < 0L)

        // Timeout or pass (no answer) yields 0 XP
        val noAnswerXp = BattleScoring.calculateXpDelta(
            isCorrect = false,
            hasAnswer = false,
            difficulty = QuizDifficulty.HARD,
            mode = QuizMode.STANDARD,
            hintRevealed = false,
            secondsRemaining = 0,
            maxSeconds = BattleScoring.BATTLE_QUESTION_SECONDS,
            newStreak = 0
        )
        assertEquals(0L, noAnswerXp)
    }

    @Test
    fun technologyCategory_isInPlayableGridWithTrilingualSeedsAndExplanations() {
        assertTrue(TriviaCategory.playableGridCategories.contains(TriviaCategory.TECHNOLOGY))
        assertEquals(TriviaCategory.TECHNOLOGY, TriviaCategory.fromApiKey("technology"))

        val techSeeds = OfflineTriviaSeed.initialSeedQuestions()
            .filter { !it.pictureQuestion && it.category == TriviaCategory.TECHNOLOGY.apiKey }
        assertTrue("Expected at least 50 Technology seeds, got ${techSeeds.size}", techSeeds.size >= 50)
        techSeeds.forEach { entity ->
            assertFalse("Missing EN explanation on ${entity.id}", entity.explanation.isNullOrBlank())
            val es = LocalizedSeedRegistry.getLocalizedCopy(entity.id, "es")
            val id = LocalizedSeedRegistry.getLocalizedCopy(entity.id, "in")
            assertNotNull("Missing ES Technology copy for ${entity.id}", es)
            assertNotNull("Missing ID Technology copy for ${entity.id}", id)
            assertFalse("Blank ES explanation for ${entity.id}", es!!.explanation.isNullOrBlank())
            assertFalse("Blank ID explanation for ${entity.id}", id!!.explanation.isNullOrBlank())
        }
    }

    @Test
    fun onlineTriviaSources_detectsFallbackExplanationsAndExtractsQuestionTopics() {
        assertTrue(OnlineTriviaSources.isPlaceholderOrFallbackExplanation(null))
        assertTrue(OnlineTriviaSources.isPlaceholderOrFallbackExplanation(""))
        assertTrue(OnlineTriviaSources.isPlaceholderOrFallbackExplanation("The correct answer is Paris."))
        assertTrue(OnlineTriviaSources.isPlaceholderOrFallbackExplanation("La respuesta correcta es París."))
        assertTrue(OnlineTriviaSources.isPlaceholderOrFallbackExplanation("Jawaban yang benar adalah Paris."))
        assertTrue(OnlineTriviaSources.isPlaceholderOrFallbackExplanation("Key fact (Science): \"Au\" correctly resolves \"symbol\"."))
        assertTrue(OnlineTriviaSources.isPlaceholderOrFallbackExplanation("Explanation (Science): Shorter blue wavelengths scatter more easily."))
        assertFalse(
            OnlineTriviaSources.isPlaceholderOrFallbackExplanation(
                "HTTP uses TCP port 80 by default whereas HTTPS wraps traffic inside TLS encryption."
            )
        )

        val topic = OnlineTriviaSources.extractSearchTopicFromQuestion(
            "In what year did Tim Berners-Lee propose the World Wide Web?"
        )
        assertNotNull(topic)
        assertTrue(topic!!.contains("Tim"))
    }

    @Test
    fun onlineTriviaSources_handlesConceptualAndExplanationQuestionsAcrossLanguages() {
        // Entity names & short concept titles remain searchable as direct subjects
        assertTrue(OnlineTriviaSources.isSearchableSubject("Marie Curie"))
        assertTrue(OnlineTriviaSources.isSearchableSubject("Rayleigh scattering"))
        assertTrue(OnlineTriviaSources.isSearchableSubject("Photosynthesis"))

        // Explanatory clauses, long sentences, numbers, and booleans are NOT sent as exact Wiki titles
        assertFalse(OnlineTriviaSources.isSearchableSubject("Because ice is less dense than liquid water"))
        assertFalse(OnlineTriviaSources.isSearchableSubject("It converts sunlight into glucose"))
        assertFalse(OnlineTriviaSources.isSearchableSubject("To absorb ultraviolet radiation"))
        assertFalse(OnlineTriviaSources.isSearchableSubject("Karena es lebih ringan dari air cair"))
        assertFalse(OnlineTriviaSources.isSearchableSubject("1969"))
        assertFalse(OnlineTriviaSources.isSearchableSubject("True"))

        // Scaffolding words stripped while keeping 3-letter conceptual nouns in natural order
        assertEquals(
            "ice float water",
            OnlineTriviaSources.extractSearchTopicFromQuestion("Why does ice float on water?")
        )
        assertEquals(
            "hot air balloons rise",
            OnlineTriviaSources.extractSearchTopicFromQuestion("Which of the following best explains why hot air balloons rise?")
        )
        assertEquals(
            "cielo azul",
            OnlineTriviaSources.extractSearchTopicFromQuestion("¿Por qué el cielo es azul?")
        )
        assertEquals(
            "langit tampak biru",
            OnlineTriviaSources.extractSearchTopicFromQuestion("Mengapa langit tampak biru?")
        )

        // Multi-query escalation combines question concept + novel answer terms first
        val queries = OnlineTriviaSources.buildWikiSearchQueries(
            questionText = "Why does the sky appear blue during the day?",
            correctAnswer = "Shorter blue wavelengths undergo Rayleigh scattering in air"
        )
        assertTrue(queries.isNotEmpty())
        assertTrue(queries.first().contains("sky"))
        assertTrue(queries.first().contains("blue"))
        assertTrue(queries.any { it.contains("Rayleigh") || it.contains("wavelengths") })

        // Grammar-aware fallback avoids awkward quoted-clause template
        val clauseFallback = OnlineTriviaSources.buildContextualFallbackExplanation(
            questionText = "Why does ice float on water?",
            answer = "because hydrogen bonds form a less dense crystalline lattice",
            category = TriviaCategory.SCIENCE,
            languageCode = "en"
        )
        assertTrue(clauseFallback.startsWith("Explanation (Science):"))
        assertFalse(clauseFallback.contains("correctly resolves"))
        assertTrue(OnlineTriviaSources.isPlaceholderOrFallbackExplanation(clauseFallback))
    }

    @Test
    fun progressionSystem_dailyStreakTracksOpensConsumesFreezesAndResetsOnGap() {
        // Day 1: first open -> 1-day streak
        val day1 = ProgressionSystem.applyDailyCheckIn(
            current = ProgressionSystem.StreakSnapshot(streakFreezes = 2),
            todayDayKey = "2026-11-20"
        )
        assertTrue(day1.checkedInToday)
        assertEquals(1, day1.updated.dailyStreak)
        assertEquals(1, day1.updated.bestDailyStreak)
        assertEquals("2026-11-20", day1.updated.lastActiveDay)

        // Same day reopen -> no duplicate increment
        val sameDay = ProgressionSystem.applyDailyCheckIn(
            current = day1.updated,
            todayDayKey = "2026-11-20"
        )
        assertFalse(sameDay.checkedInToday)
        assertEquals(1, sameDay.updated.dailyStreak)

        // Consecutive day -> increments to 2
        val day2 = ProgressionSystem.applyDailyCheckIn(
            current = sameDay.updated,
            todayDayKey = "2026-11-21"
        )
        assertTrue(day2.checkedInToday)
        assertEquals(2, day2.updated.dailyStreak)
        assertEquals(2, day2.updated.bestDailyStreak)

        // Missed Nov 22 -> opens Nov 23 (gap = 2 days, missedDays = 1) with 2 freezes -> saves streak & uses 1 freeze
        val saved = ProgressionSystem.applyDailyCheckIn(
            current = day2.updated,
            todayDayKey = "2026-11-23"
        )
        assertEquals(1, saved.freezesConsumed)
        assertFalse(saved.streakLost)
        assertEquals(3, saved.updated.dailyStreak)
        assertEquals(1, saved.updated.streakFreezes)
        assertEquals("2026-11-23", saved.updated.lastFreezeUsedDay)

        // Missed 2 days (Nov 24 & 25), opens Nov 26 with only 1 freeze left -> streak resets to 1, freezes = 0
        val reset = ProgressionSystem.applyDailyCheckIn(
            current = saved.updated,
            todayDayKey = "2026-11-26"
        )
        assertTrue(reset.streakLost)
        assertEquals(1, reset.updated.dailyStreak)
        assertEquals(3, reset.updated.bestDailyStreak)
        assertEquals(0, reset.updated.streakFreezes)
    }

    @Test
    fun progressionSystem_awardsStreakFreezesByLevelUpAnd10kDailyXpCappedAtThree() {
        val day = "2026-11-22"
        val start = ProgressionSystem.StreakSnapshot(
            dailyStreak = 2,
            bestDailyStreak = 2,
            streakFreezes = 0,
            lastActiveDay = day,
            dailyXpEarned = 0L,
            dailyXpDate = day
        )

        // Below 10,000 daily XP threshold (and no level up) -> 0 freezes
        val step1 = ProgressionSystem.applyXpForStreakFreezes(
            current = start,
            earnedXp = 8_000L,
            prevLifetimeXp = 10_500L, // already Lv.2 (Lv.2 threshold = 30,000 total)
            todayDayKey = day
        )
        assertEquals(0, step1.totalFreezesEarned)
        assertEquals(8_000L, step1.updated.dailyXpEarned)

        // Reaching >= 10,000 daily XP -> grants 1 freeze
        val step2 = ProgressionSystem.applyXpForStreakFreezes(
            current = step1.updated,
            earnedXp = 2_200L,
            prevLifetimeXp = 18_500L, // stays in Lv.2
            todayDayKey = day
        )
        assertTrue(step2.freezeEarnedFromDailyXp)
        assertEquals(1, step2.updated.streakFreezes)

        // More XP same day without leveling up -> daily XP freeze only granted once per day
        val step3 = ProgressionSystem.applyXpForStreakFreezes(
            current = step2.updated,
            earnedXp = 1_600L,
            prevLifetimeXp = 20_700L,
            todayDayKey = day
        )
        assertFalse(step3.freezeEarnedFromDailyXp)
        assertEquals(1, step3.updated.streakFreezes)

        // Crossing 30,000 lifetime XP -> Lv. 2 -> Lv. 3 grants +1 freeze
        val levelUp = ProgressionSystem.applyXpForStreakFreezes(
            current = step3.updated,
            earnedXp = 500L,
            prevLifetimeXp = 29_700L,
            todayDayKey = day
        )
        assertEquals(1, levelUp.freezesEarnedFromLevelUp)
        assertEquals(2, levelUp.updated.streakFreezes)

        // Another day earning 2,000 XP and leveling up again when at 2 freezes -> capped cleanly at 3
        val capped = ProgressionSystem.applyXpForStreakFreezes(
            current = levelUp.updated,
            earnedXp = 2_000L,
            prevLifetimeXp = 69_000L, // crosses 70,000 -> Lv. 3 -> Lv. 4
            todayDayKey = "2026-11-23"
        )
        assertEquals(ProgressionSystem.MAX_STREAK_FREEZES, capped.updated.streakFreezes)
        assertEquals(1, capped.totalFreezesEarned)
    }

    @Test
    fun wikipediaRepository_parsesAvailableGeminiModelsFromApiResponse() {
        val sampleResponse = """
            {
              "models": [
                {
                  "name": "models/embedding-001",
                  "displayName": "Embedding 001",
                  "supportedGenerationMethods": ["embedContent"]
                },
                {
                  "name": "models/gemini-custom-preview",
                  "displayName": "Gemini Custom Preview",
                  "description": "Returned dynamically from key",
                  "supportedGenerationMethods": ["generateContent", "countTokens"]
                },
                {
                  "name": "models/gemini-fast-next",
                  "displayName": "",
                  "supportedGenerationMethods": ["generateContent"]
                }
              ]
            }
        """.trimIndent()

        val parsed = WikipediaRepository.parseAvailableModelsJson(sampleResponse)
        assertEquals(2, parsed.size)
        assertEquals("gemini-custom-preview", parsed[0].modelId)
        assertEquals("Gemini Custom Preview", parsed[0].displayName)
        assertEquals("gemini-fast-next", parsed[1].modelId)
        assertEquals("gemini-fast-next", parsed[1].displayName)
        assertTrue(WikipediaRepository.parseAvailableModelsJson("{}").isEmpty())
    }

    @Test
    fun wikipediaRepository_normalizesUrlsAndExtractsWikipediaTargets() {
        assertEquals(
            "https://en.wikipedia.org/wiki/Ada_Lovelace",
            WikipediaRepository.normalizeWebUrl("en.wikipedia.org/wiki/Ada_Lovelace")
        )
        assertEquals(null, WikipediaRepository.normalizeWebUrl("not a valid url"))

        val enTarget = WikipediaRepository.extractWikipediaUrlTarget("https://en.wikipedia.org/wiki/Ada_Lovelace#Legacy")
        assertEquals("en" to "Ada Lovelace", enTarget)

        val esMobileTarget = WikipediaRepository.extractWikipediaUrlTarget("https://es.m.wikipedia.org/wiki/Fotos%C3%ADntesis")
        assertEquals("es" to "Fotosíntesis", esMobileTarget)

        val nonWiki = WikipediaRepository.extractWikipediaUrlTarget("https://www.bbc.com/news/science-123456")
        assertEquals(null, nonWiki)
    }

    @Test
    fun wikipediaRepository_extractsReadableArticleFromWebHtml() {
        val html = """
            <html>
              <head>
                <title>Fallback Title</title>
                <meta property="og:title" content="Deep Sea Exploration &amp; Hydrothermal Vents" />
                <meta property="og:description" content="How submersibles map the ocean floor." />
                <meta property="og:image" content="https://example.com/ocean.jpg" />
              </head>
              <body>
                <nav><a href="/home">Menu navigation links ignored</a></nav>
                <script>var secret = "ignored script content";</script>
                <article>
                  <h2>Early Submersibles</h2>
                  <p>In 1960, Jacques Piccard and Don Walsh reached the Challenger Deep aboard the bathyscaphe Trieste.</p>
                  <p>Hydrothermal vents were first discovered along the Galapagos Rift in 1977, revealing ecosystems powered by chemosynthesis.</p>
                </article>
                <footer>Copyright Notice</footer>
              </body>
            </html>
        """.trimIndent()

        val article = WikipediaRepository.extractReadableWebArticle(
            html = html,
            sourceUrl = "https://example.com/articles/deep-sea",
            languageCode = "en",
            dateKey = "2026-11-23"
        )
        assertNotNull(article)
        assertTrue(article!!.pageId < 0L)
        assertEquals("Deep Sea Exploration & Hydrothermal Vents", article.title)
        assertEquals("How submersibles map the ocean floor.", article.description)
        assertEquals("https://example.com/ocean.jpg", article.thumbnailUrl)
        assertTrue(article.extract.contains("== Early Submersibles =="))
        assertTrue(article.extract.contains("Challenger Deep"))
        assertFalse(article.extract.contains("ignored script content"))
        assertFalse(article.extract.contains("Menu navigation links"))
    }

    @Test
    fun localGeminiKeyVault_redactsSecretsFromMessages() {
        val secret = "AIzaSyTestSecretKey12345"
        val error = "Request failed using key AIzaSyTestSecretKey12345 on server"
        val redacted = LocalGeminiKeyVault.redactSecret(error, secret)
        assertFalse(redacted.contains(secret))
        assertTrue(redacted.contains("[REDACTED]"))
        assertEquals("Plain message", LocalGeminiKeyVault.redactSecret("Plain message", secret))
    }

    @Test
    fun dailyObjectives_has30UniqueScenariosAndDeterministic3BucketDailySelection() {
        assertEquals(30, DailyObjectives.allScenarios.size)
        assertEquals(30, DailyObjectives.allScenarios.map { it.id }.distinct().size)

        val day1UserA = DailyObjectives.scenariosForDay("2026-11-23", "user_alpha")
        val day1UserAAgain = DailyObjectives.scenariosForDay("2026-11-23", "user_alpha")
        assertEquals(3, day1UserA.size)
        assertEquals(day1UserA.map { it.id }, day1UserAAgain.map { it.id })
        assertEquals(3, day1UserA.map { it.bucket }.distinct().size)

        val day2UserA = DailyObjectives.scenariosForDay("2026-11-24", "user_alpha")
        assertEquals(3, day2UserA.size)
        assertEquals(3, day2UserA.map { it.bucket }.distinct().size)

        // Different users on the exact same day get different daily objective sets
        val sampleUsers = listOf("user_alpha", "user_beta", "user_gamma", "user_delta", "user_epsilon", "user_zeta")
        val distinctSets = sampleUsers.map { uid ->
            DailyObjectives.scenariosForDay("2026-11-23", uid).map { it.id }
        }.distinct()
        assertTrue("Expected distinct objective sets across users, got ${distinctSets.size}", distinctSets.size >= 4)
    }

    @Test
    fun dailyObjectives_completingAllThreeFor3ConsecutiveDaysAwardsStreakFreeze() {
        val days = listOf("2026-11-23", "2026-11-24", "2026-11-25")
        val testUserKey = "user_streak_tester"
        var snapshot = ProgressionSystem.StreakSnapshot(dailyStreak = 1, bestDailyStreak = 1, lastActiveDay = days[0])
        var lifetimeXp = 10_500L // Start safely in Lv.2 so level-ups don't award freezes

        days.forEachIndexed { dayIndex, dayKey ->
            val scenarios = DailyObjectives.scenariosForDay(dayKey, testUserKey)
            scenarios.forEachIndexed { idx, scenario ->
                val result = buildRoundThatSatisfies(scenario)
                val outcome = ProgressionSystem.applyXpForStreakFreezes(
                    current = snapshot,
                    earnedXp = result.score,
                    prevLifetimeXp = lifetimeXp,
                    todayDayKey = dayKey,
                    completedRound = result,
                    hintsUsed = 0,
                    userKey = testUserKey
                )
                lifetimeXp += result.score
                snapshot = outcome.updated

                if (dayIndex < 2) {
                    assertFalse(outcome.freezeEarnedFromObjectives)
                } else if (idx == 2) {
                    assertTrue(outcome.freezeEarnedFromObjectives)
                    assertEquals(1, snapshot.streakFreezes)
                    assertEquals(3, snapshot.effectiveObjectiveStreak(dayKey))
                }
            }
            assertEquals(3, snapshot.completedObjectivesFor(dayKey).size)
            assertEquals(dayIndex + 1, snapshot.dailyObjectiveStreak)
        }

        // Next day after 3-day payout resets display cycle to 0 until all 3 objectives are finished again
        assertEquals(0, snapshot.effectiveObjectiveStreak("2026-11-26"))
    }

    private fun buildRoundThatSatisfies(scenario: DailyObjectiveScenario): QuizRoundResult {
        val base = when (val spec = scenario.launchSpec) {
            is ObjectiveLaunchSpec.Quiz -> QuizRoundResult(
                category = spec.category,
                difficulty = spec.difficulty,
                mode = spec.mode,
                score = 1_600L,
                correctCount = spec.questionCount,
                totalQuestions = spec.questionCount,
                bestStreak = spec.questionCount,
                durationSeconds = 45,
                playedOffline = false
            )
            ObjectiveLaunchSpec.FeaturedWikipedia -> QuizRoundResult(
                category = TriviaCategory.WIKIPEDIA,
                difficulty = QuizDifficulty.ALL,
                mode = QuizMode.STANDARD,
                score = 900L,
                correctCount = 5,
                totalQuestions = 5,
                bestStreak = 5,
                durationSeconds = 40,
                playedOffline = false
            )
        }
        return when (scenario.id) {
            "xp_round_1200" -> base.copy(score = 1_300L)
            "xp_hard_1500" -> base.copy(difficulty = QuizDifficulty.HARD, score = 1_600L)
            "daily_xp_3000" -> base.copy(score = 3_200L)
            else -> base
        }
    }
}
