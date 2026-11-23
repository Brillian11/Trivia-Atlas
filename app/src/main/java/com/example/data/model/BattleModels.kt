package com.example.data.model

import androidx.annotation.Keep
import com.example.data.local.OfflineTriviaSeed
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class BattleStatus(val key: String) {
    WAITING("waiting"),
    ACTIVE("active"),
    FINISHED("finished");

    companion object {
        fun fromKey(raw: String?): BattleStatus =
            entries.firstOrNull { it.key == raw?.lowercase() } ?: WAITING
    }
}

enum class BattleInviteStatus(val key: String) {
    PENDING("pending"),
    ACCEPTED("accepted"),
    DECLINED("declined"),
    CANCELLED("cancelled");

    companion object {
        fun fromKey(raw: String?): BattleInviteStatus =
            entries.firstOrNull { it.key == raw?.lowercase() } ?: PENDING
    }
}

@Keep
@IgnoreExtraProperties
data class BattleQuestionDto(
    val id: String = "",
    val category: String = TriviaCategory.GENERAL_KNOWLEDGE.apiKey,
    val difficulty: String = QuizDifficulty.MEDIUM.apiKey,
    val questionText: String = "",
    val correctAnswer: String = "",
    val allOptions: List<String> = emptyList(),
    val pictureQuestion: Boolean = false,
    val imageUrl: String = "",
    val imageDrawableName: String = "",
    val explanation: String = ""
) {
    fun toTriviaQuestion(): TriviaQuestion = TriviaQuestion(
        id = id,
        category = TriviaCategory.fromApiKey(category),
        difficulty = QuizDifficulty.fromKey(difficulty).let {
            if (it == QuizDifficulty.ALL) QuizDifficulty.MEDIUM else it
        },
        questionText = questionText,
        correctAnswer = correctAnswer,
        allOptions = allOptions.ifEmpty { listOf(correctAnswer) },
        pictureQuestion = pictureQuestion,
        imageDrawableRes = OfflineTriviaSeed.drawableResByName(imageDrawableName.takeIf { it.isNotBlank() }),
        imageUrl = imageUrl.takeIf { it.isNotBlank() },
        illustrationTag = null,
        explanation = explanation.takeIf { it.isNotBlank() }
    )
}

fun TriviaQuestion.toBattleQuestionMap(): Map<String, Any> = mapOf(
    "id" to id,
    "category" to category.apiKey,
    "difficulty" to difficulty.apiKey,
    "questionText" to questionText,
    "correctAnswer" to correctAnswer,
    "allOptions" to allOptions,
    "pictureQuestion" to pictureQuestion,
    "imageUrl" to (imageUrl ?: ""),
    "imageDrawableName" to (OfflineTriviaSeed.drawableNameByRes(imageDrawableRes) ?: ""),
    "explanation" to (explanation ?: correctAnswer)
)

@Keep
@IgnoreExtraProperties
data class BattleRoom(
    @DocumentId val id: String = "",
    val roomCode: String = "",
    val hostUserId: String = "",
    val hostDisplayName: String = "Atlas Explorer",
    val hostAvatarId: String = AvatarOption.COMPASS.id,
    val topics: List<String> = emptyList(),
    val difficulty: String = QuizDifficulty.ALL.apiKey,
    val mode: String = QuizMode.STANDARD.key,
    val questionCount: Int = 5,
    val languageCode: String = "en",
    val status: String = BattleStatus.WAITING.key,
    val memberUids: List<String> = emptyList(),
    val questions: List<BattleQuestionDto> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    val battleStatus: BattleStatus get() = BattleStatus.fromKey(status)
    val quizDifficulty: QuizDifficulty get() = QuizDifficulty.fromKey(difficulty)
    val quizMode: QuizMode get() = QuizMode.fromKey(mode)
    val topicCategories: List<TriviaCategory>
        get() = topics.map { TriviaCategory.fromApiKey(it) }.distinct()
}

@Keep
@IgnoreExtraProperties
data class BattleParticipant(
    val userId: String = "",
    val displayName: String = "Explorer",
    val handle: String = "atlas_user",
    val avatarId: String = AvatarOption.COMPASS.id,
    val score: Long = 0L,
    val correctCount: Int = 0,
    val answeredCount: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val durationSeconds: Int = 0,
    val finished: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class BattleInvite(
    @DocumentId val id: String = "",
    val roomId: String = "",
    val roomCode: String = "",
    val hostUserId: String = "",
    val hostDisplayName: String = "Explorer",
    val hostAvatarId: String = AvatarOption.COMPASS.id,
    val inviteeUserId: String = "",
    val inviteeDisplayName: String = "Friend",
    val topics: List<String> = emptyList(),
    val difficulty: String = QuizDifficulty.ALL.apiKey,
    val mode: String = QuizMode.STANDARD.key,
    val questionCount: Int = 5,
    val status: String = BattleInviteStatus.PENDING.key,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    val inviteStatus: BattleInviteStatus get() = BattleInviteStatus.fromKey(status)
    val topicCategories: List<TriviaCategory>
        get() = topics.map { TriviaCategory.fromApiKey(it) }.distinct()
    val quizMode: QuizMode get() = QuizMode.fromKey(mode)
}
