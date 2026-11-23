package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cached_questions",
    indices = [
        Index(value = ["category", "difficulty", "pictureQuestion"]),
        Index(value = ["timesServed", "lastServedAt"])
    ]
)
data class CachedQuestionEntity(
    @PrimaryKey val id: String,
    val category: String,
    val difficulty: String,
    val questionText: String,
    val correctAnswer: String,
    val incorrectAnswersSerialized: String,
    val pictureQuestion: Boolean = false,
    val imageDrawableName: String? = null,
    val imageUrl: String? = null,
    val illustrationTag: String? = null,
    val explanation: String? = null,
    val timesServed: Int = 0,
    val lastServedAt: Long = 0L,
    val cachedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "pending_scores")
data class PendingScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: String,
    val category: String,
    val difficulty: String,
    val mode: String,
    val score: Long,
    val correctCount: Int,
    val totalQuestions: Int,
    val bestStreak: Int,
    val durationSeconds: Int,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "guest_sessions")
data class GuestSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val category: String,
    val difficulty: String,
    val mode: String,
    val score: Long,
    val correctCount: Int,
    val totalQuestions: Int,
    val bestStreak: Int,
    val durationSeconds: Int,
    val playedOffline: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)

data class CategoryCacheCount(
    val category: String,
    val count: Int
)
