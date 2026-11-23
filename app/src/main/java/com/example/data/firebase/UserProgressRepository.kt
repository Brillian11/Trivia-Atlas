package com.example.data.firebase

import android.content.Context
import com.example.R
import com.example.data.model.AvatarOption
import com.example.data.model.LeaderboardEntry
import com.example.data.model.ProgressionSystem
import com.example.data.model.QuizRoundResult
import com.example.data.model.QuizSession
import com.example.data.model.TriviaCategory
import com.example.data.model.UserProfile
import com.example.data.model.WeeklyRankingEntry
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserProgressRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String =
        auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")

    fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        val docRef = db.collection("users").document(userId)
        emitAll(
            docRef.snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(
                            UserProfile::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } else {
                        null
                    }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val ex = firestoreError ?: (error as? Exception)
                    if (ex != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(ex, OperationType.GET, docRef.path)
                    }
                    throw error
                }
        )
    }

    fun observeQuizSessions(userId: String): Flow<List<QuizSession>> = flow {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        val colRef = db.collection("users").document(userId).collection("quiz_sessions")
        emitAll(
            colRef.orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(25)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        QuizSession::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val ex = firestoreError ?: (error as? Exception)
                    if (ex != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(ex, OperationType.LIST, colRef.path)
                    }
                    throw error
                }
        )
    }

    fun observeWeeklyLeaderboard(cycleId: String): Flow<List<WeeklyRankingEntry>> = flow {
        val uid = requireUserId()
        require(cycleId.isNotBlank()) { "Cycle ID must not be blank" }
        val colPath = "weekly_rankings/$cycleId/entries"
        val query = db.collection("weekly_rankings")
            .document(cycleId)
            .collection("entries")
            .whereGreaterThanOrEqualTo("weeklyXp", 0L)
            .orderBy("weeklyXp", Query.Direction.DESCENDING)
            .limit(50)

        emitAll(
            query.snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        WeeklyRankingEntry::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val ex = firestoreError ?: (error as? Exception)
                    if (ex != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(ex, OperationType.LIST, colPath)
                    }
                    throw error
                }
        )
    }

    fun observeUserWeeklyRanking(userId: String, cycleId: String): Flow<WeeklyRankingEntry?> = flow {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        require(cycleId.isNotBlank()) { "Cycle ID must not be blank" }
        val docRef = db.collection("weekly_rankings")
            .document(cycleId)
            .collection("entries")
            .document(userId)

        emitAll(
            docRef.snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(
                            WeeklyRankingEntry::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } else {
                        null
                    }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val ex = firestoreError ?: (error as? Exception)
                    if (ex != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(ex, OperationType.GET, docRef.path)
                    }
                    throw error
                }
        )
    }

    fun observeGlobalLeaderboard(): Flow<List<LeaderboardEntry>> = flow {
        val uid = requireUserId()
        val query = db.collection("leaderboard")
            .whereGreaterThanOrEqualTo("totalScore", 0L)
            .orderBy("totalScore", Query.Direction.DESCENDING)
            .limit(50)

        emitAll(
            query.snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        LeaderboardEntry::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val ex = firestoreError ?: (error as? Exception)
                    if (ex != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(ex, OperationType.LIST, "leaderboard")
                    }
                    throw error
                }
        )
    }

    suspend fun ensureProfileInitialized(
        userId: String,
        googleDisplayName: String?
    ): Result<Unit> {
        val userDocPath = "users/$userId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Signed-in user mismatch" }

            val userRef = db.collection("users").document(uid)
            val lbRef = db.collection("leaderboard").document(uid)
            val snapshot = userRef.get().await()

            if (!snapshot.exists()) {
                val safeName = sanitizeDisplayName(googleDisplayName)
                val safeHandle = sanitizeHandle(safeName, uid)
                val todayKey = ProgressionSystem.localDayKey()
                val profilePayload = mapOf(
                    "userId" to uid,
                    "displayName" to safeName,
                    "handle" to safeHandle,
                    "avatarId" to AvatarOption.COMPASS.id,
                    "bio" to "Exploring trivia across the globe.",
                    "totalScore" to 0L,
                    "highScore" to 0L,
                    "gamesPlayed" to 0L,
                    "totalCorrect" to 0L,
                    "totalAnswered" to 0L,
                    "bestStreak" to 0L,
                    "favoriteCategory" to TriviaCategory.GENERAL_KNOWLEDGE.apiKey,
                    "dailyStreak" to 1L,
                    "bestDailyStreak" to 1L,
                    "streakFreezes" to 0L,
                    "lastActiveDay" to todayKey,
                    "lastFreezeUsedDay" to "",
                    "dailyXpEarned" to 0L,
                    "dailyXpDate" to todayKey,
                    "dailyXpFreezeGrantedDate" to "",
                    "objectiveDayKey" to todayKey,
                    "completedObjectiveIds" to emptyList<String>(),
                    "dailyRoundsCompleted" to 0L,
                    "dailyObjectiveStreak" to 0L,
                    "lastObjectiveCompletedDay" to "",
                    "objectiveFreezeGrantedDate" to "",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                val lbPayload = mapOf(
                    "userId" to uid,
                    "displayName" to safeName,
                    "handle" to safeHandle,
                    "avatarId" to AvatarOption.COMPASS.id,
                    "totalScore" to 0L,
                    "highScore" to 0L,
                    "gamesPlayed" to 0L,
                    "accuracyPercent" to 0L,
                    "bestStreak" to 0L,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                db.runBatch { batch ->
                    batch.set(userRef, profilePayload)
                    batch.set(lbRef, lbPayload)
                }.await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, userDocPath)
            Result.failure(e)
        }
    }

    suspend fun recordDailyCheckIn(
        userId: String,
        fallbackDisplayName: String? = null
    ): Result<ProgressionSystem.DailyCheckInOutcome> {
        val userDocPath = "users/$userId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Signed-in user mismatch" }

            val userRef = db.collection("users").document(uid)
            val lbRef = db.collection("leaderboard").document(uid)
            val todayKey = ProgressionSystem.localDayKey()

            val outcome = db.runTransaction { transaction ->
                val profileSnap = transaction.get(userRef)
                val lbSnap = transaction.get(lbRef)

                if (!profileSnap.exists()) {
                    val safeName = sanitizeDisplayName(fallbackDisplayName)
                    val safeHandle = sanitizeHandle(safeName, uid)
                    val initialOutcome = ProgressionSystem.applyDailyCheckIn(
                        current = ProgressionSystem.StreakSnapshot(),
                        todayDayKey = todayKey
                    )
                    val s = initialOutcome.updated
                    transaction.set(
                        userRef,
                        mapOf(
                            "userId" to uid,
                            "displayName" to safeName,
                            "handle" to safeHandle,
                            "avatarId" to AvatarOption.COMPASS.id,
                            "bio" to "Exploring trivia across the globe.",
                            "totalScore" to 0L,
                            "highScore" to 0L,
                            "gamesPlayed" to 0L,
                            "totalCorrect" to 0L,
                            "totalAnswered" to 0L,
                            "bestStreak" to 0L,
                            "favoriteCategory" to TriviaCategory.GENERAL_KNOWLEDGE.apiKey,
                            "dailyStreak" to s.dailyStreak.toLong(),
                            "bestDailyStreak" to s.bestDailyStreak.toLong(),
                            "streakFreezes" to s.streakFreezes.toLong(),
                            "lastActiveDay" to s.lastActiveDay,
                            "lastFreezeUsedDay" to s.lastFreezeUsedDay,
                            "dailyXpEarned" to s.dailyXpEarned,
                            "dailyXpDate" to s.dailyXpDate,
                            "dailyXpFreezeGrantedDate" to s.dailyXpFreezeGrantedDate,
                            "objectiveDayKey" to s.objectiveDayKey,
                            "completedObjectiveIds" to s.completedObjectiveIds,
                            "dailyRoundsCompleted" to s.dailyRoundsCompleted.toLong(),
                            "dailyObjectiveStreak" to s.dailyObjectiveStreak.toLong(),
                            "lastObjectiveCompletedDay" to s.lastObjectiveCompletedDay,
                            "objectiveFreezeGrantedDate" to s.objectiveFreezeGrantedDate,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                    if (!lbSnap.exists()) {
                        transaction.set(
                            lbRef,
                            mapOf(
                                "userId" to uid,
                                "displayName" to safeName,
                                "handle" to safeHandle,
                                "avatarId" to AvatarOption.COMPASS.id,
                                "totalScore" to 0L,
                                "highScore" to 0L,
                                "gamesPlayed" to 0L,
                                "accuracyPercent" to 0L,
                                "bestStreak" to 0L,
                                "createdAt" to FieldValue.serverTimestamp(),
                                "updatedAt" to FieldValue.serverTimestamp()
                            )
                        )
                    }
                    return@runTransaction initialOutcome
                }

                val prevSnap = extractStreakSnapshot(profileSnap)
                val checkInOutcome = ProgressionSystem.applyDailyCheckIn(prevSnap, todayKey)
                val s = checkInOutcome.updated
                if (s != prevSnap) {
                    transaction.update(
                        userRef,
                        mapOf(
                            "dailyStreak" to s.dailyStreak.toLong(),
                            "bestDailyStreak" to s.bestDailyStreak.toLong(),
                            "streakFreezes" to s.streakFreezes.toLong(),
                            "lastActiveDay" to s.lastActiveDay,
                            "lastFreezeUsedDay" to s.lastFreezeUsedDay,
                            "dailyXpEarned" to s.dailyXpEarned,
                            "dailyXpDate" to s.dailyXpDate,
                            "dailyXpFreezeGrantedDate" to s.dailyXpFreezeGrantedDate,
                            "objectiveDayKey" to s.objectiveDayKey,
                            "completedObjectiveIds" to s.completedObjectiveIds,
                            "dailyRoundsCompleted" to s.dailyRoundsCompleted.toLong(),
                            "dailyObjectiveStreak" to s.dailyObjectiveStreak.toLong(),
                            "lastObjectiveCompletedDay" to s.lastObjectiveCompletedDay,
                            "objectiveFreezeGrantedDate" to s.objectiveFreezeGrantedDate,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                }
                checkInOutcome
            }.await()

            Result.success(outcome)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, userDocPath)
            Result.failure(e)
        }
    }

    suspend fun updateCustomProfile(
        userId: String,
        displayName: String,
        handle: String,
        avatarId: AvatarOption,
        bio: String,
        favoriteCategory: TriviaCategory
    ): Result<Unit> {
        val docPath = "users/$userId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Signed-in user mismatch" }

            val cleanName = displayName.trim().take(30)
            val cleanHandle = handle.trim().removePrefix("@").replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(24)
            val cleanBio = bio.trim().take(140)
            require(cleanName.length >= 2) { "Display name must be at least 2 characters." }
            require(cleanHandle.length >= 2) { "Handle must be at least 2 characters." }

            val cycleId = ProgressionSystem.currentWeeklyCycle().cycleId
            val userRef = db.collection("users").document(uid)
            val lbRef = db.collection("leaderboard").document(uid)
            val weeklyRef = db.collection("weekly_rankings")
                .document(cycleId)
                .collection("entries")
                .document(uid)
            val lbSnap = lbRef.get().await()
            val weeklySnap = weeklyRef.get().await()

            db.runBatch { batch ->
                batch.update(
                    userRef,
                    mapOf(
                        "displayName" to cleanName,
                        "handle" to cleanHandle,
                        "avatarId" to avatarId.id,
                        "bio" to cleanBio,
                        "favoriteCategory" to favoriteCategory.apiKey,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                if (lbSnap.exists()) {
                    batch.update(
                        lbRef,
                        mapOf(
                            "displayName" to cleanName,
                            "handle" to cleanHandle,
                            "avatarId" to avatarId.id,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                }
                if (weeklySnap.exists()) {
                    batch.update(
                        weeklyRef,
                        mapOf(
                            "displayName" to cleanName,
                            "handle" to cleanHandle,
                            "avatarId" to avatarId.id,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                }
            }.await()

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun recordQuizResult(
        userId: String,
        result: QuizRoundResult,
        fallbackDisplayName: String? = null,
        hintsUsed: Int = 0
    ): Result<ProgressionSystem.FreezeRewardOutcome> {
        val userPath = "users/$userId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Signed-in user mismatch" }
            require(result.totalQuestions in 1..50) { "Total questions out of bounds" }
            require(result.correctCount in 0..result.totalQuestions) { "Correct count out of bounds" }
            require(result.score in 0L..50000L) { "Score out of bounds" }

            val cycleId = ProgressionSystem.currentWeeklyCycle().cycleId
            val todayKey = ProgressionSystem.localDayKey()
            val userRef = db.collection("users").document(uid)
            val lbRef = db.collection("leaderboard").document(uid)
            val weeklyRef = db.collection("weekly_rankings")
                .document(cycleId)
                .collection("entries")
                .document(uid)
            val sessionRef = userRef.collection("quiz_sessions").document()

            val rewardOutcome = db.runTransaction { transaction ->
                val profileSnap = transaction.get(userRef)
                val lbSnap = transaction.get(lbRef)
                val weeklySnap = transaction.get(weeklyRef)

                val existingDisplayName = profileSnap.getString("displayName")
                    ?: sanitizeDisplayName(fallbackDisplayName)
                val existingHandle = profileSnap.getString("handle")
                    ?: sanitizeHandle(existingDisplayName, uid)
                val existingAvatar = profileSnap.getString("avatarId")
                    ?: AvatarOption.COMPASS.id
                val existingBio = profileSnap.getString("bio")
                    ?: "Exploring trivia across the globe."
                val existingFavCat = profileSnap.getString("favoriteCategory")
                    ?: result.category.apiKey

                val prevTotalScore = profileSnap.getLong("totalScore") ?: 0L
                val prevHighScore = profileSnap.getLong("highScore") ?: 0L
                val prevGamesPlayed = profileSnap.getLong("gamesPlayed") ?: 0L
                val prevTotalCorrect = profileSnap.getLong("totalCorrect") ?: 0L
                val prevTotalAnswered = profileSnap.getLong("totalAnswered") ?: 0L
                val prevBestStreak = profileSnap.getLong("bestStreak") ?: 0L
                val prevStreakSnapshot = extractStreakSnapshot(profileSnap)

                val freezeOutcome = ProgressionSystem.applyXpForStreakFreezes(
                    current = prevStreakSnapshot,
                    earnedXp = result.score,
                    prevLifetimeXp = prevTotalScore,
                    todayDayKey = todayKey,
                    completedRound = result,
                    hintsUsed = hintsUsed,
                    userKey = uid
                )
                val nextStreak = freezeOutcome.updated

                val newTotalScore = prevTotalScore + result.score
                val newHighScore = maxOf(prevHighScore, result.score)
                val newGamesPlayed = prevGamesPlayed + 1L
                val newTotalCorrect = prevTotalCorrect + result.correctCount.toLong()
                val newTotalAnswered = prevTotalAnswered + result.totalQuestions.toLong()
                val newBestStreak = maxOf(prevBestStreak, result.bestStreak.toLong())
                val newAccuracy = if (newTotalAnswered <= 0L) {
                    0L
                } else {
                    ((newTotalCorrect * 100.0) / newTotalAnswered).toLong().coerceIn(0L, 100L)
                }
                val newLifetimeLevel = ProgressionSystem.calculateLevelProgress(newTotalScore).level.toLong()

                val prevWeeklyXp = weeklySnap.getLong("weeklyXp") ?: 0L
                val prevWeeklyHigh = weeklySnap.getLong("highScore") ?: 0L
                val prevWeeklyGames = weeklySnap.getLong("gamesPlayed") ?: 0L
                val prevWeeklyCorrect = weeklySnap.getLong("correctCount") ?: 0L
                val prevWeeklyAnswered = weeklySnap.getLong("answeredCount") ?: 0L
                val prevWeeklyStreak = weeklySnap.getLong("bestStreak") ?: 0L

                val newWeeklyXp = prevWeeklyXp + result.score
                val newWeeklyHigh = maxOf(prevWeeklyHigh, result.score)
                val newWeeklyGames = prevWeeklyGames + 1L
                val newWeeklyCorrect = prevWeeklyCorrect + result.correctCount.toLong()
                val newWeeklyAnswered = prevWeeklyAnswered + result.totalQuestions.toLong()
                val newWeeklyStreak = maxOf(prevWeeklyStreak, result.bestStreak.toLong())
                val newWeeklyAccuracy = if (newWeeklyAnswered <= 0L) {
                    0L
                } else {
                    ((newWeeklyCorrect * 100.0) / newWeeklyAnswered).toLong().coerceIn(0L, 100L)
                }

                if (!profileSnap.exists()) {
                    transaction.set(
                        userRef,
                        mapOf(
                            "userId" to uid,
                            "displayName" to existingDisplayName,
                            "handle" to existingHandle,
                            "avatarId" to existingAvatar,
                            "bio" to existingBio,
                            "totalScore" to newTotalScore,
                            "highScore" to newHighScore,
                            "gamesPlayed" to newGamesPlayed,
                            "totalCorrect" to newTotalCorrect,
                            "totalAnswered" to newTotalAnswered,
                            "bestStreak" to newBestStreak,
                            "favoriteCategory" to existingFavCat,
                            "dailyStreak" to nextStreak.dailyStreak.toLong(),
                            "bestDailyStreak" to nextStreak.bestDailyStreak.toLong(),
                            "streakFreezes" to nextStreak.streakFreezes.toLong(),
                            "lastActiveDay" to nextStreak.lastActiveDay,
                            "lastFreezeUsedDay" to nextStreak.lastFreezeUsedDay,
                            "dailyXpEarned" to nextStreak.dailyXpEarned,
                            "dailyXpDate" to nextStreak.dailyXpDate,
                            "dailyXpFreezeGrantedDate" to nextStreak.dailyXpFreezeGrantedDate,
                            "objectiveDayKey" to nextStreak.objectiveDayKey,
                            "completedObjectiveIds" to nextStreak.completedObjectiveIds,
                            "dailyRoundsCompleted" to nextStreak.dailyRoundsCompleted.toLong(),
                            "dailyObjectiveStreak" to nextStreak.dailyObjectiveStreak.toLong(),
                            "lastObjectiveCompletedDay" to nextStreak.lastObjectiveCompletedDay,
                            "objectiveFreezeGrantedDate" to nextStreak.objectiveFreezeGrantedDate,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                } else {
                    transaction.update(
                        userRef,
                        mapOf(
                            "totalScore" to newTotalScore,
                            "highScore" to newHighScore,
                            "gamesPlayed" to newGamesPlayed,
                            "totalCorrect" to newTotalCorrect,
                            "totalAnswered" to newTotalAnswered,
                            "bestStreak" to newBestStreak,
                            "dailyStreak" to nextStreak.dailyStreak.toLong(),
                            "bestDailyStreak" to nextStreak.bestDailyStreak.toLong(),
                            "streakFreezes" to nextStreak.streakFreezes.toLong(),
                            "lastActiveDay" to nextStreak.lastActiveDay,
                            "lastFreezeUsedDay" to nextStreak.lastFreezeUsedDay,
                            "dailyXpEarned" to nextStreak.dailyXpEarned,
                            "dailyXpDate" to nextStreak.dailyXpDate,
                            "dailyXpFreezeGrantedDate" to nextStreak.dailyXpFreezeGrantedDate,
                            "objectiveDayKey" to nextStreak.objectiveDayKey,
                            "completedObjectiveIds" to nextStreak.completedObjectiveIds,
                            "dailyRoundsCompleted" to nextStreak.dailyRoundsCompleted.toLong(),
                            "dailyObjectiveStreak" to nextStreak.dailyObjectiveStreak.toLong(),
                            "lastObjectiveCompletedDay" to nextStreak.lastObjectiveCompletedDay,
                            "objectiveFreezeGrantedDate" to nextStreak.objectiveFreezeGrantedDate,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                }

                if (!lbSnap.exists()) {
                    transaction.set(
                        lbRef,
                        mapOf(
                            "userId" to uid,
                            "displayName" to existingDisplayName,
                            "handle" to existingHandle,
                            "avatarId" to existingAvatar,
                            "totalScore" to newTotalScore,
                            "highScore" to newHighScore,
                            "gamesPlayed" to newGamesPlayed,
                            "accuracyPercent" to newAccuracy,
                            "bestStreak" to newBestStreak,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                } else {
                    transaction.update(
                        lbRef,
                        mapOf(
                            "displayName" to existingDisplayName,
                            "handle" to existingHandle,
                            "avatarId" to existingAvatar,
                            "totalScore" to newTotalScore,
                            "highScore" to newHighScore,
                            "gamesPlayed" to newGamesPlayed,
                            "accuracyPercent" to newAccuracy,
                            "bestStreak" to newBestStreak,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                }

                if (!weeklySnap.exists()) {
                    transaction.set(
                        weeklyRef,
                        mapOf(
                            "userId" to uid,
                            "cycleId" to cycleId,
                            "displayName" to existingDisplayName,
                            "handle" to existingHandle,
                            "avatarId" to existingAvatar,
                            "weeklyXp" to newWeeklyXp,
                            "highScore" to newWeeklyHigh,
                            "gamesPlayed" to newWeeklyGames,
                            "correctCount" to newWeeklyCorrect,
                            "answeredCount" to newWeeklyAnswered,
                            "accuracyPercent" to newWeeklyAccuracy,
                            "bestStreak" to newWeeklyStreak,
                            "lifetimeXp" to newTotalScore,
                            "level" to newLifetimeLevel,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                } else {
                    transaction.update(
                        weeklyRef,
                        mapOf(
                            "displayName" to existingDisplayName,
                            "handle" to existingHandle,
                            "avatarId" to existingAvatar,
                            "weeklyXp" to newWeeklyXp,
                            "highScore" to newWeeklyHigh,
                            "gamesPlayed" to newWeeklyGames,
                            "correctCount" to newWeeklyCorrect,
                            "answeredCount" to newWeeklyAnswered,
                            "accuracyPercent" to newWeeklyAccuracy,
                            "bestStreak" to newWeeklyStreak,
                            "lifetimeXp" to newTotalScore,
                            "level" to newLifetimeLevel,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                }

                transaction.set(
                    sessionRef,
                    mapOf(
                        "category" to result.category.apiKey,
                        "difficulty" to result.difficulty.apiKey,
                        "mode" to result.mode.key,
                        "score" to result.score,
                        "correctCount" to result.correctCount.toLong(),
                        "totalQuestions" to result.totalQuestions.toLong(),
                        "bestStreak" to result.bestStreak.toLong(),
                        "durationSeconds" to result.durationSeconds.coerceIn(0, 3600).toLong(),
                        "playedOffline" to result.playedOffline,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )
                freezeOutcome
            }.await()

            Result.success(rewardOutcome)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, userPath)
            Result.failure(e)
        }
    }

    private fun extractStreakSnapshot(profileSnap: DocumentSnapshot): ProgressionSystem.StreakSnapshot {
        if (!profileSnap.exists()) return ProgressionSystem.StreakSnapshot()
        val rawObjectiveIds = (profileSnap.get("completedObjectiveIds") as? List<*>)
            ?.mapNotNull { it as? String }
            ?.filter { it.isNotBlank() }
            ?.distinct()
            ?.take(3)
            .orEmpty()
        return ProgressionSystem.StreakSnapshot(
            dailyStreak = (profileSnap.getLong("dailyStreak") ?: 0L).toInt().coerceAtLeast(0),
            bestDailyStreak = (profileSnap.getLong("bestDailyStreak") ?: 0L).toInt().coerceAtLeast(0),
            streakFreezes = (profileSnap.getLong("streakFreezes") ?: 0L).toInt()
                .coerceIn(0, ProgressionSystem.MAX_STREAK_FREEZES),
            lastActiveDay = profileSnap.getString("lastActiveDay").orEmpty(),
            lastFreezeUsedDay = profileSnap.getString("lastFreezeUsedDay").orEmpty(),
            dailyXpEarned = (profileSnap.getLong("dailyXpEarned") ?: 0L).coerceAtLeast(0L),
            dailyXpDate = profileSnap.getString("dailyXpDate").orEmpty(),
            dailyXpFreezeGrantedDate = profileSnap.getString("dailyXpFreezeGrantedDate").orEmpty(),
            objectiveDayKey = profileSnap.getString("objectiveDayKey").orEmpty(),
            completedObjectiveIds = rawObjectiveIds,
            dailyRoundsCompleted = (profileSnap.getLong("dailyRoundsCompleted") ?: 0L).toInt().coerceAtLeast(0),
            dailyObjectiveStreak = (profileSnap.getLong("dailyObjectiveStreak") ?: 0L).toInt().coerceAtLeast(0),
            lastObjectiveCompletedDay = profileSnap.getString("lastObjectiveCompletedDay").orEmpty(),
            objectiveFreezeGrantedDate = profileSnap.getString("objectiveFreezeGrantedDate").orEmpty()
        )
    }

    private fun sanitizeDisplayName(raw: String?): String {
        val trimmed = raw?.trim().orEmpty().take(30)
        return if (trimmed.length >= 2) trimmed else "Atlas Explorer"
    }

    private fun sanitizeHandle(displayName: String, uid: String): String {
        val base = displayName.lowercase()
            .replace(Regex("[^a-z0-9]"), "_")
            .replace(Regex("_+"), "_")
            .trim('_')
            .take(16)
        val suffix = uid.take(4).lowercase().replace(Regex("[^a-z0-9]"), "x")
        val combined = if (base.length >= 2) "${base}_$suffix" else "explorer_$suffix"
        return combined.take(24)
    }
}
