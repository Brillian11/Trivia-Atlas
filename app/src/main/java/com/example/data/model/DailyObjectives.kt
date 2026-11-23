package com.example.data.model

import androidx.annotation.StringRes
import com.example.R

enum class ObjectiveBucket {
    CATEGORY_MASTERY,
    CHALLENGE_FEAT,
    EXPLORATION_HABIT
}

sealed interface ObjectiveLaunchSpec {
    data class Quiz(
        val category: TriviaCategory,
        val difficulty: QuizDifficulty = QuizDifficulty.ALL,
        val mode: QuizMode = QuizMode.STANDARD,
        val questionCount: Int = 10
    ) : ObjectiveLaunchSpec

    data object FeaturedWikipedia : ObjectiveLaunchSpec
}

data class DailyObjectiveContext(
    val result: QuizRoundResult,
    val hintsUsed: Int = 0,
    val dailyRoundsCompleted: Int = 1,
    val dailyXpTotal: Long = result.score
)

data class DailyObjectiveScenario(
    val id: String,
    val bucket: ObjectiveBucket,
    @StringRes val titleRes: Int,
    @StringRes val badgeRes: Int,
    val launchSpec: ObjectiveLaunchSpec,
    val isSatisfiedBy: (DailyObjectiveContext) -> Boolean
)

data class DailyObjectiveProgressItem(
    val scenario: DailyObjectiveScenario,
    val completed: Boolean,
    val currentNumericProgress: Int? = null,
    val targetNumericProgress: Int? = null
)

object DailyObjectives {

    val allScenarios: List<DailyObjectiveScenario> = listOf(
        // --- Bucket 1: Category Mastery (10 scenarios) ---
        DailyObjectiveScenario(
            id = "geo_10_10",
            bucket = ObjectiveBucket.CATEGORY_MASTERY,
            titleRes = R.string.obj_geo_10_10,
            badgeRes = R.string.cat_geography_title,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.GEOGRAPHY, QuizDifficulty.ALL, QuizMode.STANDARD, 10),
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.GEOGRAPHY &&
                    ctx.result.totalQuestions >= 10 &&
                    ctx.result.correctCount >= 10
            }
        ),
        DailyObjectiveScenario(
            id = "sci_10_10",
            bucket = ObjectiveBucket.CATEGORY_MASTERY,
            titleRes = R.string.obj_sci_10_10,
            badgeRes = R.string.cat_science_title,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.SCIENCE, QuizDifficulty.ALL, QuizMode.STANDARD, 10),
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.SCIENCE &&
                    ctx.result.totalQuestions >= 10 &&
                    ctx.result.correctCount >= 10
            }
        ),
        DailyObjectiveScenario(
            id = "his_10_10",
            bucket = ObjectiveBucket.CATEGORY_MASTERY,
            titleRes = R.string.obj_his_10_10,
            badgeRes = R.string.cat_history_title,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.HISTORY, QuizDifficulty.ALL, QuizMode.STANDARD, 10),
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.HISTORY &&
                    ctx.result.totalQuestions >= 10 &&
                    ctx.result.correctCount >= 10
            }
        ),
        DailyObjectiveScenario(
            id = "tech_10_10",
            bucket = ObjectiveBucket.CATEGORY_MASTERY,
            titleRes = R.string.obj_tech_10_10,
            badgeRes = R.string.cat_technology_title,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.TECHNOLOGY, QuizDifficulty.ALL, QuizMode.STANDARD, 10),
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.TECHNOLOGY &&
                    ctx.result.totalQuestions >= 10 &&
                    ctx.result.correctCount >= 10
            }
        ),
        DailyObjectiveScenario(
            id = "gk_10_10",
            bucket = ObjectiveBucket.CATEGORY_MASTERY,
            titleRes = R.string.obj_gk_10_10,
            badgeRes = R.string.cat_general_title,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.GENERAL_KNOWLEDGE, QuizDifficulty.ALL, QuizMode.STANDARD, 10),
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.GENERAL_KNOWLEDGE &&
                    ctx.result.totalQuestions >= 10 &&
                    ctx.result.correctCount >= 10
            }
        ),
        DailyObjectiveScenario(
            id = "arts_8_10",
            bucket = ObjectiveBucket.CATEGORY_MASTERY,
            titleRes = R.string.obj_arts_8_10,
            badgeRes = R.string.cat_arts_title,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.ARTS_AND_LITERATURE, QuizDifficulty.ALL, QuizMode.STANDARD, 10),
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.ARTS_AND_LITERATURE &&
                    ctx.result.totalQuestions >= 10 &&
                    ctx.result.correctCount >= 8
            }
        ),
        DailyObjectiveScenario(
            id = "film_8_10",
            bucket = ObjectiveBucket.CATEGORY_MASTERY,
            titleRes = R.string.obj_film_8_10,
            badgeRes = R.string.cat_film_title,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.FILM_AND_TV, QuizDifficulty.ALL, QuizMode.STANDARD, 10),
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.FILM_AND_TV &&
                    ctx.result.totalQuestions >= 10 &&
                    ctx.result.correctCount >= 8
            }
        ),
        DailyObjectiveScenario(
            id = "music_8_10",
            bucket = ObjectiveBucket.CATEGORY_MASTERY,
            titleRes = R.string.obj_music_8_10,
            badgeRes = R.string.cat_music_title,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.MUSIC, QuizDifficulty.ALL, QuizMode.STANDARD, 10),
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.MUSIC &&
                    ctx.result.totalQuestions >= 10 &&
                    ctx.result.correctCount >= 8
            }
        ),
        DailyObjectiveScenario(
            id = "sport_8_10",
            bucket = ObjectiveBucket.CATEGORY_MASTERY,
            titleRes = R.string.obj_sport_8_10,
            badgeRes = R.string.cat_sport_title,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.SPORT_AND_LEISURE, QuizDifficulty.ALL, QuizMode.STANDARD, 10),
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.SPORT_AND_LEISURE &&
                    ctx.result.totalQuestions >= 10 &&
                    ctx.result.correctCount >= 8
            }
        ),
        DailyObjectiveScenario(
            id = "food_8_10",
            bucket = ObjectiveBucket.CATEGORY_MASTERY,
            titleRes = R.string.obj_food_8_10,
            badgeRes = R.string.cat_food_title,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.FOOD_AND_DRINK, QuizDifficulty.ALL, QuizMode.STANDARD, 10),
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.FOOD_AND_DRINK &&
                    ctx.result.totalQuestions >= 10 &&
                    ctx.result.correctCount >= 8
            }
        ),

        // --- Bucket 2: Difficulty, Speed, Streak & Visual Feats (10 scenarios) ---
        DailyObjectiveScenario(
            id = "culture_8_10",
            bucket = ObjectiveBucket.CHALLENGE_FEAT,
            titleRes = R.string.obj_culture_8_10,
            badgeRes = R.string.cat_culture_title,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.SOCIETY_AND_CULTURE, QuizDifficulty.ALL, QuizMode.STANDARD, 10),
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.SOCIETY_AND_CULTURE &&
                    ctx.result.totalQuestions >= 10 &&
                    ctx.result.correctCount >= 8
            }
        ),
        DailyObjectiveScenario(
            id = "hard_mix_70",
            bucket = ObjectiveBucket.CHALLENGE_FEAT,
            titleRes = R.string.obj_hard_mix_70,
            badgeRes = R.string.diff_hard,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.MIXED, QuizDifficulty.HARD, QuizMode.STANDARD, 10),
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.MIXED &&
                    ctx.result.difficulty == QuizDifficulty.HARD &&
                    ctx.result.totalQuestions >= 5 &&
                    accuracyPercent(ctx.result) >= 70
            }
        ),
        DailyObjectiveScenario(
            id = "hard_sci_80",
            bucket = ObjectiveBucket.CHALLENGE_FEAT,
            titleRes = R.string.obj_hard_sci_80,
            badgeRes = R.string.diff_hard,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.SCIENCE, QuizDifficulty.HARD, QuizMode.STANDARD, 5),
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.SCIENCE &&
                    ctx.result.difficulty == QuizDifficulty.HARD &&
                    ctx.result.totalQuestions >= 5 &&
                    accuracyPercent(ctx.result) >= 80
            }
        ),
        DailyObjectiveScenario(
            id = "hard_his_80",
            bucket = ObjectiveBucket.CHALLENGE_FEAT,
            titleRes = R.string.obj_hard_his_80,
            badgeRes = R.string.diff_hard,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.HISTORY, QuizDifficulty.HARD, QuizMode.STANDARD, 5),
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.HISTORY &&
                    ctx.result.difficulty == QuizDifficulty.HARD &&
                    ctx.result.totalQuestions >= 5 &&
                    accuracyPercent(ctx.result) >= 80
            }
        ),
        DailyObjectiveScenario(
            id = "speed_geo_90",
            bucket = ObjectiveBucket.CHALLENGE_FEAT,
            titleRes = R.string.obj_speed_geo_90,
            badgeRes = R.string.obj_badge_speed,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.GEOGRAPHY, QuizDifficulty.ALL, QuizMode.STANDARD, 10),
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.GEOGRAPHY &&
                    ctx.result.totalQuestions >= 10 &&
                    ctx.result.correctCount >= 6 &&
                    ctx.result.durationSeconds <= 90
            }
        ),
        DailyObjectiveScenario(
            id = "speed_tech_90",
            bucket = ObjectiveBucket.CHALLENGE_FEAT,
            titleRes = R.string.obj_speed_tech_90,
            badgeRes = R.string.obj_badge_speed,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.TECHNOLOGY, QuizDifficulty.ALL, QuizMode.STANDARD, 10),
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.TECHNOLOGY &&
                    ctx.result.totalQuestions >= 10 &&
                    ctx.result.correctCount >= 6 &&
                    ctx.result.durationSeconds <= 90
            }
        ),
        DailyObjectiveScenario(
            id = "streak_mix_7",
            bucket = ObjectiveBucket.CHALLENGE_FEAT,
            titleRes = R.string.obj_streak_mix_7,
            badgeRes = R.string.obj_badge_streak,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.MIXED, QuizDifficulty.ALL, QuizMode.STANDARD, 10),
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.MIXED &&
                    ctx.result.bestStreak >= 7
            }
        ),
        DailyObjectiveScenario(
            id = "streak_any_10",
            bucket = ObjectiveBucket.CHALLENGE_FEAT,
            titleRes = R.string.obj_streak_any_10,
            badgeRes = R.string.obj_badge_streak,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.MIXED, QuizDifficulty.ALL, QuizMode.STANDARD, 10),
            isSatisfiedBy = { ctx ->
                ctx.result.totalQuestions >= 10 &&
                    ctx.result.bestStreak >= 10
            }
        ),
        DailyObjectiveScenario(
            id = "pic_8_10",
            bucket = ObjectiveBucket.CHALLENGE_FEAT,
            titleRes = R.string.obj_pic_8_10,
            badgeRes = R.string.cat_picture_title,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.PICTURE_ATLAS, QuizDifficulty.ALL, QuizMode.PICTURE, 10),
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.PICTURE_ATLAS &&
                    ctx.result.totalQuestions >= 10 &&
                    ctx.result.correctCount >= 8
            }
        ),
        DailyObjectiveScenario(
            id = "pic_10_10",
            bucket = ObjectiveBucket.CHALLENGE_FEAT,
            titleRes = R.string.obj_pic_10_10,
            badgeRes = R.string.cat_picture_title,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.PICTURE_ATLAS, QuizDifficulty.ALL, QuizMode.PICTURE, 10),
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.PICTURE_ATLAS &&
                    ctx.result.totalQuestions >= 10 &&
                    ctx.result.correctCount >= 10
            }
        ),

        // --- Bucket 3: Guess Mode, Wikipedia & Daily Habits (10 scenarios) ---
        DailyObjectiveScenario(
            id = "pic_guess_70",
            bucket = ObjectiveBucket.EXPLORATION_HABIT,
            titleRes = R.string.obj_pic_guess_70,
            badgeRes = R.string.mode_guess,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.PICTURE_ATLAS, QuizDifficulty.ALL, QuizMode.GUESS, 8),
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.PICTURE_ATLAS &&
                    ctx.result.mode == QuizMode.GUESS &&
                    ctx.result.totalQuestions >= 5 &&
                    accuracyPercent(ctx.result) >= 70
            }
        ),
        DailyObjectiveScenario(
            id = "guess_mix_6",
            bucket = ObjectiveBucket.EXPLORATION_HABIT,
            titleRes = R.string.obj_guess_mix_6,
            badgeRes = R.string.mode_guess,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.MIXED, QuizDifficulty.ALL, QuizMode.GUESS, 10),
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.MIXED &&
                    ctx.result.mode == QuizMode.GUESS &&
                    ctx.result.correctCount >= 6
            }
        ),
        DailyObjectiveScenario(
            id = "guess_geo_no_hint",
            bucket = ObjectiveBucket.EXPLORATION_HABIT,
            titleRes = R.string.obj_guess_geo_no_hint,
            badgeRes = R.string.mode_guess,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.GEOGRAPHY, QuizDifficulty.ALL, QuizMode.GUESS, 10),
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.GEOGRAPHY &&
                    ctx.result.mode == QuizMode.GUESS &&
                    ctx.result.correctCount >= 5 &&
                    ctx.hintsUsed == 0
            }
        ),
        DailyObjectiveScenario(
            id = "guess_sci_70",
            bucket = ObjectiveBucket.EXPLORATION_HABIT,
            titleRes = R.string.obj_guess_sci_70,
            badgeRes = R.string.mode_guess,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.SCIENCE, QuizDifficulty.ALL, QuizMode.GUESS, 5),
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.SCIENCE &&
                    ctx.result.mode == QuizMode.GUESS &&
                    ctx.result.totalQuestions >= 5 &&
                    accuracyPercent(ctx.result) >= 70
            }
        ),
        DailyObjectiveScenario(
            id = "wiki_70",
            bucket = ObjectiveBucket.EXPLORATION_HABIT,
            titleRes = R.string.obj_wiki_70,
            badgeRes = R.string.cat_wikipedia_title,
            launchSpec = ObjectiveLaunchSpec.FeaturedWikipedia,
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.WIKIPEDIA &&
                    ctx.result.totalQuestions >= 3 &&
                    accuracyPercent(ctx.result) >= 70
            }
        ),
        DailyObjectiveScenario(
            id = "wiki_100",
            bucket = ObjectiveBucket.EXPLORATION_HABIT,
            titleRes = R.string.obj_wiki_100,
            badgeRes = R.string.cat_wikipedia_title,
            launchSpec = ObjectiveLaunchSpec.FeaturedWikipedia,
            isSatisfiedBy = { ctx ->
                ctx.result.category == TriviaCategory.WIKIPEDIA &&
                    ctx.result.totalQuestions >= 3 &&
                    ctx.result.correctCount == ctx.result.totalQuestions
            }
        ),
        DailyObjectiveScenario(
            id = "xp_round_1200",
            bucket = ObjectiveBucket.EXPLORATION_HABIT,
            titleRes = R.string.obj_xp_round_1200,
            badgeRes = R.string.obj_badge_xp,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.GEOGRAPHY, QuizDifficulty.ALL, QuizMode.STANDARD, 10),
            isSatisfiedBy = { ctx ->
                (ctx.result.category == TriviaCategory.GEOGRAPHY || ctx.result.category == TriviaCategory.HISTORY) &&
                    ctx.result.score >= 1_200L
            }
        ),
        DailyObjectiveScenario(
            id = "xp_hard_1500",
            bucket = ObjectiveBucket.EXPLORATION_HABIT,
            titleRes = R.string.obj_xp_hard_1500,
            badgeRes = R.string.obj_badge_xp,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.MIXED, QuizDifficulty.HARD, QuizMode.STANDARD, 10),
            isSatisfiedBy = { ctx ->
                ctx.result.difficulty == QuizDifficulty.HARD &&
                    ctx.result.score >= 1_500L
            }
        ),
        DailyObjectiveScenario(
            id = "rounds_2_today",
            bucket = ObjectiveBucket.EXPLORATION_HABIT,
            titleRes = R.string.obj_rounds_2_today,
            badgeRes = R.string.obj_badge_habit,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.MIXED, QuizDifficulty.ALL, QuizMode.STANDARD, 10),
            isSatisfiedBy = { ctx ->
                ctx.dailyRoundsCompleted >= 2
            }
        ),
        DailyObjectiveScenario(
            id = "daily_xp_3000",
            bucket = ObjectiveBucket.EXPLORATION_HABIT,
            titleRes = R.string.obj_daily_xp_3000,
            badgeRes = R.string.obj_badge_xp,
            launchSpec = ObjectiveLaunchSpec.Quiz(TriviaCategory.MIXED, QuizDifficulty.ALL, QuizMode.STANDARD, 10),
            isSatisfiedBy = { ctx ->
                ctx.dailyXpTotal >= 3_000L
            }
        )
    )

    private val scenariosByBucket: Map<ObjectiveBucket, List<DailyObjectiveScenario>> =
        allScenarios.groupBy { it.bucket }

    /**
     * Deterministically selects 3 varied daily scenarios (1 per bucket) for [dayKey] (`yyyy-MM-dd`)
     * personalized per [userKey] so different users get different daily objectives on the same day.
     */
    fun scenariosForDay(dayKey: String, userKey: String = ""): List<DailyObjectiveScenario> {
        val hash = deterministicSeed(dayKey, userKey)
        val aList = scenariosByBucket.getValue(ObjectiveBucket.CATEGORY_MASTERY)
        val bList = scenariosByBucket.getValue(ObjectiveBucket.CHALLENGE_FEAT)
        val cList = scenariosByBucket.getValue(ObjectiveBucket.EXPLORATION_HABIT)

        val rng = java.util.Random(hash)
        val first = aList[rng.nextInt(aList.size)]
        val second = bList[rng.nextInt(bList.size)]
        val third = cList[rng.nextInt(cList.size)]
        return listOf(first, second, third)
    }

    fun progressItemsForDay(
        snapshot: ProgressionSystem.StreakSnapshot,
        todayDayKey: String,
        userKey: String = ""
    ): List<DailyObjectiveProgressItem> {
        val scenarios = scenariosForDay(todayDayKey, userKey)
        val completedIds = snapshot.completedObjectivesFor(todayDayKey).toSet()
        val roundsToday = snapshot.roundsCompletedOn(todayDayKey)
        val xpToday = snapshot.dailyXpFor(todayDayKey).toInt().coerceAtMost(3_000)

        return scenarios.map { scenario ->
            val completed = scenario.id in completedIds
            val (cur, target) = when (scenario.id) {
                "rounds_2_today" -> (if (completed) 2 else roundsToday.coerceAtMost(2)) to 2
                "daily_xp_3000" -> (if (completed) 3_000 else xpToday) to 3_000
                else -> null to null
            }
            DailyObjectiveProgressItem(
                scenario = scenario,
                completed = completed,
                currentNumericProgress = cur,
                targetNumericProgress = target
            )
        }
    }

    private fun deterministicSeed(dayKey: String, userKey: String): Long {
        val combined = "$dayKey|${userKey.ifBlank { "default" }}"
        var h = -0x340d631b7bdddcdbL
        for (ch in combined) {
            h = h xor ch.code.toLong()
            h *= 0x100000001b3L
        }
        // SplitMix64 finalizer for strong dispersion across similar user keys
        h = (h xor (h ushr 30)) * 0xbf58476d1ce4e5b9UL.toLong()
        h = (h xor (h ushr 27)) * 0x94d049bb133111ebUL.toLong()
        return h xor (h ushr 31)
    }

    private fun accuracyPercent(result: QuizRoundResult): Int {
        if (result.totalQuestions <= 0) return 0
        return (result.correctCount * 100) / result.totalQuestions
    }
}
