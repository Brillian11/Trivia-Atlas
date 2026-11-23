package com.example.data.local

data class LocalizedQuestionCopy(
    val questionText: String,
    val correctAnswer: String,
    val incorrectAnswers: List<String>,
    val explanation: String
)

object LocalizedSeedRegistry {
    fun getLocalizedCopy(id: String, languageCode: String): LocalizedQuestionCopy? {
        return when (languageCode.lowercase()) {
            "es" -> LocalizedSeedEs.questions[id]
                ?: OfflineFactBank.getLocalizedCopy(id, "es")
                ?: OfflineTechnologyBank.getLocalizedCopy(id, "es")
            "in", "id" -> LocalizedSeedIn.questions[id]
                ?: OfflineFactBank.getLocalizedCopy(id, "in")
                ?: OfflineTechnologyBank.getLocalizedCopy(id, "in")
            else -> null
        }
    }
}
