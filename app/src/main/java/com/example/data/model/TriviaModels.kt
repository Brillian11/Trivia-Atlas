package com.example.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.example.R
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class AvatarOption(val id: String, val label: String, @StringRes val labelRes: Int) {
    COMPASS("compass", "Compass", R.string.avatar_compass),
    OWL("owl", "Scholar Owl", R.string.avatar_owl),
    TELESCOPE("telescope", "Stargazer", R.string.avatar_telescope),
    GLOBE("globe", "Cartographer", R.string.avatar_globe),
    QUILL("quill", "Archivist", R.string.avatar_quill),
    ATOM("atom", "Alchemist", R.string.avatar_atom);

    companion object {
        fun fromId(raw: String?): AvatarOption =
            entries.firstOrNull { it.id == raw } ?: COMPASS
    }
}

enum class ThemeMode(val key: String, val label: String, @StringRes val labelRes: Int) {
    SYSTEM("system", "Auto", R.string.theme_system),
    LIGHT("light", "Daylight", R.string.theme_light),
    DARK("dark", "Night", R.string.theme_dark);

    companion object {
        fun fromKey(raw: String?): ThemeMode =
            entries.firstOrNull { it.key == raw } ?: SYSTEM
    }
}

enum class AppLanguage(val code: String, val shortBadge: String, @StringRes val labelRes: Int) {
    SYSTEM("system", "AUTO", R.string.lang_system),
    ENGLISH("en", "EN", R.string.lang_en),
    SPANISH("es", "ES", R.string.lang_es),
    INDONESIAN("in", "ID", R.string.lang_in);

    companion object {
        fun fromCode(raw: String?): AppLanguage = when (raw?.lowercase()) {
            "en" -> ENGLISH
            "es" -> SPANISH
            "in", "id" -> INDONESIAN
            else -> SYSTEM
        }
    }
}

data class GuestStatsSummary(
    val totalScore: Long = 0L,
    val highScore: Long = 0L,
    val gamesPlayed: Long = 0L,
    val totalCorrect: Long = 0L,
    val totalAnswered: Long = 0L,
    val bestStreak: Long = 0L
) {
    val accuracyPercent: Int
        get() = if (totalAnswered <= 0L) 0 else ((totalCorrect * 100.0) / totalAnswered).toInt().coerceIn(0, 100)
}

data class TriviaQuestion(
    val id: String,
    val category: TriviaCategory,
    val difficulty: QuizDifficulty,
    val questionText: String,
    val correctAnswer: String,
    val allOptions: List<String>,
    val pictureQuestion: Boolean = false,
    @DrawableRes val imageDrawableRes: Int? = null,
    val imageUrl: String? = null,
    val illustrationTag: String? = null,
    val explanation: String? = null
)

@JsonClass(generateAdapter = true)
data class TriviaApiQuestionTextDto(
    @Json(name = "text") val text: String? = null,
    @Json(name = "image") val image: String? = null
)

@JsonClass(generateAdapter = true)
data class TriviaApiQuestionDto(
    @Json(name = "id") val id: String,
    @Json(name = "category") val category: String = "general_knowledge",
    @Json(name = "correctAnswer") val correctAnswer: String = "",
    @Json(name = "incorrectAnswers") val incorrectAnswers: List<String> = emptyList(),
    @Json(name = "question") val question: TriviaApiQuestionTextDto? = null,
    @Json(name = "difficulty") val difficulty: String? = "medium",
    @Json(name = "type") val type: String? = "text_choice",
    @Json(name = "tags") val tags: List<String> = emptyList(),
    @Json(name = "imageUrl") val imageUrl: String? = null,
    @Json(name = "explanation") val explanation: String? = null
)

@Keep
@IgnoreExtraProperties
data class UserProfile(
    val userId: String = "",
    val displayName: String = "Explorer",
    val handle: String = "atlas_user",
    val avatarId: String = AvatarOption.COMPASS.id,
    val bio: String = "Exploring trivia across the globe.",
    val totalScore: Long = 0L,
    val highScore: Long = 0L,
    val gamesPlayed: Long = 0L,
    val totalCorrect: Long = 0L,
    val totalAnswered: Long = 0L,
    val bestStreak: Long = 0L,
    val favoriteCategory: String = TriviaCategory.GENERAL_KNOWLEDGE.apiKey,
    val dailyStreak: Long = 0L,
    val bestDailyStreak: Long = 0L,
    val streakFreezes: Long = 0L,
    val lastActiveDay: String = "",
    val lastFreezeUsedDay: String = "",
    val dailyXpEarned: Long = 0L,
    val dailyXpDate: String = "",
    val dailyXpFreezeGrantedDate: String = "",
    val objectiveDayKey: String = "",
    val completedObjectiveIds: List<String> = emptyList(),
    val dailyRoundsCompleted: Long = 0L,
    val dailyObjectiveStreak: Long = 0L,
    val lastObjectiveCompletedDay: String = "",
    val objectiveFreezeGrantedDate: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    val accuracyPercent: Int
        get() = if (totalAnswered <= 0L) 0 else ((totalCorrect * 100.0) / totalAnswered).toInt().coerceIn(0, 100)

    fun toStreakSnapshot(): ProgressionSystem.StreakSnapshot =
        ProgressionSystem.StreakSnapshot(
            dailyStreak = dailyStreak.toInt().coerceAtLeast(0),
            bestDailyStreak = bestDailyStreak.toInt().coerceAtLeast(0),
            streakFreezes = streakFreezes.toInt().coerceIn(0, ProgressionSystem.MAX_STREAK_FREEZES),
            lastActiveDay = lastActiveDay,
            lastFreezeUsedDay = lastFreezeUsedDay,
            dailyXpEarned = dailyXpEarned.coerceAtLeast(0L),
            dailyXpDate = dailyXpDate,
            dailyXpFreezeGrantedDate = dailyXpFreezeGrantedDate,
            objectiveDayKey = objectiveDayKey,
            completedObjectiveIds = completedObjectiveIds,
            dailyRoundsCompleted = dailyRoundsCompleted.toInt().coerceAtLeast(0),
            dailyObjectiveStreak = dailyObjectiveStreak.toInt().coerceAtLeast(0),
            lastObjectiveCompletedDay = lastObjectiveCompletedDay,
            objectiveFreezeGrantedDate = objectiveFreezeGrantedDate
        )
}

@Keep
@IgnoreExtraProperties
data class QuizSession(
    @DocumentId val id: String = "",
    val category: String = TriviaCategory.MIXED.apiKey,
    val difficulty: String = QuizDifficulty.ALL.apiKey,
    val mode: String = QuizMode.STANDARD.key,
    val score: Long = 0L,
    val correctCount: Long = 0L,
    val totalQuestions: Long = 0L,
    val bestStreak: Long = 0L,
    val durationSeconds: Long = 0L,
    val playedOffline: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class LeaderboardEntry(
    val userId: String = "",
    val displayName: String = "Explorer",
    val handle: String = "atlas_user",
    val avatarId: String = AvatarOption.COMPASS.id,
    val totalScore: Long = 0L,
    val highScore: Long = 0L,
    val gamesPlayed: Long = 0L,
    val accuracyPercent: Long = 0L,
    val bestStreak: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class WeeklyRankingEntry(
    val userId: String = "",
    val cycleId: String = "",
    val displayName: String = "Explorer",
    val handle: String = "atlas_user",
    val avatarId: String = AvatarOption.COMPASS.id,
    val weeklyXp: Long = 0L,
    val highScore: Long = 0L,
    val gamesPlayed: Long = 0L,
    val correctCount: Long = 0L,
    val answeredCount: Long = 0L,
    val accuracyPercent: Long = 0L,
    val bestStreak: Long = 0L,
    val lifetimeXp: Long = 0L,
    val level: Long = 1L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class QuizRoundResult(
    val category: TriviaCategory,
    val difficulty: QuizDifficulty,
    val mode: QuizMode,
    val score: Long,
    val correctCount: Int,
    val totalQuestions: Int,
    val bestStreak: Int,
    val durationSeconds: Int,
    val playedOffline: Boolean
)

data class QuestionReviewItem(
    val index: Int,
    val question: TriviaQuestion,
    val userAnswer: String?,
    val wasCorrect: Boolean,
    val timedOut: Boolean,
    val usedHint: Boolean,
    val pointsEarned: Long,
    val explanation: String
)

object AnswerMatcher {
    fun normalize(raw: String): String {
        val decomposed = java.text.Normalizer.normalize(raw.trim(), java.text.Normalizer.Form.NFD)
        val noAccents = decomposed.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        val alnum = noAccents.lowercase()
            .replace(Regex("[^a-z0-9\\s]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
        return alnum
            .removePrefix("the ")
            .removePrefix("an ")
            .removePrefix("a ")
            .removePrefix("el ")
            .removePrefix("la ")
            .removePrefix("los ")
            .removePrefix("las ")
            .removePrefix("un ")
            .removePrefix("una ")
            .trim()
    }

    fun isGuessMatch(guess: String, correctAnswer: String): Boolean {
        val g = normalize(guess)
        val c = normalize(correctAnswer)
        if (g.isEmpty() || c.isEmpty()) return false
        if (g == c) return true

        // Numeric answers must match exactly after normalization
        if (c.all { it.isDigit() }) {
            return g == c
        }

        // Allow matching surname/key word for multi-word answers when >= 5 chars
        val cTokens = c.split(" ").filter { it.length >= 4 }
        if (cTokens.size >= 2 && g.length >= 5 && cTokens.any { it == g }) {
            return true
        }

        val maxEdits = when {
            c.length <= 4 -> 0
            c.length <= 8 -> 1
            else -> 2
        }
        return levenshtein(g, c) <= maxEdits
    }

    fun buildHintPattern(correctAnswer: String): String {
        val trimmed = correctAnswer.trim()
        val words = trimmed.split(Regex("\\s+"))
        var revealedFirst = false
        val masked = words.joinToString("   ") { word ->
            word.mapIndexed { idx, ch ->
                if (!ch.isLetterOrDigit()) {
                    ch.toString()
                } else if (!revealedFirst && idx == 0) {
                    revealedFirst = true
                    ch.uppercaseChar().toString()
                } else {
                    "_"
                }
            }.joinToString(" ")
        }
        val counts = words.joinToString(", ") { w -> "${w.count { it.isLetterOrDigit() }}" }
        return "$masked  ($counts)"
    }

    private fun levenshtein(a: String, b: String): Int {
        if (a == b) return 0
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length
        var prev = IntArray(b.length + 1) { it }
        var curr = IntArray(b.length + 1)
        for (i in a.indices) {
            curr[0] = i + 1
            for (j in b.indices) {
                val cost = if (a[i] == b[j]) 0 else 1
                curr[j + 1] = minOf(
                    curr[j] + 1,
                    prev[j + 1] + 1,
                    prev[j] + cost
                )
            }
            val tmp = prev
            prev = curr
            curr = tmp
        }
        return prev[b.length]
    }
}
