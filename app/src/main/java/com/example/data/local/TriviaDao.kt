package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TriviaDao {
    @Query("SELECT COUNT(*) FROM cached_questions")
    fun observeTotalCachedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM cached_questions WHERE pictureQuestion = 1")
    fun observePictureQuestionCount(): Flow<Int>

    @Query("SELECT category, COUNT(*) AS count FROM cached_questions GROUP BY category")
    fun observeCategoryCounts(): Flow<List<CategoryCacheCount>>

    @Query("SELECT COUNT(*) FROM cached_questions")
    suspend fun getTotalCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertQuestionsIgnore(questions: List<CachedQuestionEntity>)

    @Query("DELETE FROM cached_questions")
    suspend fun deleteAllCachedQuestions()

    @Query("DELETE FROM cached_questions WHERE id LIKE 'api_%'")
    suspend fun deleteApiCachedQuestions()

    @Query(
        """
        SELECT * FROM cached_questions
        WHERE pictureQuestion = 0
          AND (:category = 'mixed' OR category = :category)
          AND (:difficulty = 'all' OR difficulty = :difficulty)
        ORDER BY timesServed ASC, lastServedAt ASC, RANDOM()
        LIMIT :limit
        """
    )
    suspend fun getStandardQuestions(
        category: String,
        difficulty: String,
        limit: Int
    ): List<CachedQuestionEntity>

    @Query(
        """
        SELECT * FROM cached_questions
        WHERE pictureQuestion = 0
          AND (:category = 'mixed' OR category = :category)
        ORDER BY timesServed ASC, lastServedAt ASC, RANDOM()
        LIMIT :limit
        """
    )
    suspend fun getStandardQuestionsAnyDifficulty(
        category: String,
        limit: Int
    ): List<CachedQuestionEntity>

    @Query(
        """
        SELECT * FROM cached_questions
        WHERE pictureQuestion = 1
        ORDER BY timesServed ASC, lastServedAt ASC, RANDOM()
        LIMIT :limit
        """
    )
    suspend fun getPictureQuestions(limit: Int): List<CachedQuestionEntity>

    @Query("SELECT * FROM cached_questions WHERE pictureQuestion = 1")
    suspend fun getAllPictureQuestions(): List<CachedQuestionEntity>

    @Query(
        """
        SELECT * FROM cached_questions
        WHERE pictureQuestion = 0
          AND (:category = 'mixed' OR category = :category)
        """
    )
    suspend fun getAllStandardQuestionsForCategory(category: String): List<CachedQuestionEntity>

    @Query("UPDATE cached_questions SET timesServed = timesServed + 1, lastServedAt = :servedAt WHERE id IN (:ids)")
    suspend fun markQuestionsServed(ids: List<String>, servedAt: Long)

    @Query("SELECT COUNT(*) FROM pending_scores WHERE userId = :userId")
    fun observePendingScoreCount(userId: String): Flow<Int>

    @Query("SELECT * FROM pending_scores WHERE userId = :userId ORDER BY createdAtMillis ASC")
    suspend fun getPendingScoresForUser(userId: String): List<PendingScoreEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingScore(score: PendingScoreEntity)

    @Query("DELETE FROM pending_scores WHERE id = :id")
    suspend fun deletePendingScore(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuestSession(session: GuestSessionEntity)

    @Query("SELECT * FROM guest_sessions ORDER BY createdAtMillis DESC")
    fun observeAllGuestSessions(): Flow<List<GuestSessionEntity>>
}
