package com.example.data.firebase

import com.example.data.model.AvatarOption
import com.example.data.model.BattleInvite
import com.example.data.model.BattleInviteStatus
import com.example.data.model.BattleParticipant
import com.example.data.model.BattleRoom
import com.example.data.model.BattleStatus
import com.example.data.model.LeaderboardEntry
import com.example.data.model.QuizDifficulty
import com.example.data.model.QuizMode
import com.example.data.model.TriviaCategory
import com.example.data.model.WikipediaArticle
import com.example.data.model.toBattleQuestionMap
import com.example.data.repository.TriviaRepository
import com.example.data.repository.WikipediaRepository
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class BattleRepository(
    private val db: FirebaseFirestore,
    private val progressRepository: UserProgressRepository,
    private val triviaRepository: TriviaRepository,
    private val wikipediaRepository: WikipediaRepository? = null,
    private val auth: FirebaseAuth = Firebase.auth
) {
    private fun requireUserId(): String =
        auth.currentUser?.uid
            ?: throw IllegalStateException("Sign in with Google to play Battle Mode.")

    fun observeMyRooms(userId: String): Flow<List<BattleRoom>> = flow {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        val query = db.collection("battle_rooms")
            .whereArrayContains("memberUids", userId)
        emitAll(
            query.snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        BattleRoom::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.updatedAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val ex = firestoreError ?: (error as? Exception)
                    if (ex != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(ex, OperationType.LIST, "battle_rooms")
                    }
                    throw error
                }
        )
    }

    fun observeRoom(roomCode: String): Flow<BattleRoom?> = flow {
        val code = normalizeCode(roomCode)
        require(code.length in 4..12) { "Invalid room code" }
        val uid = requireUserId()
        val docRef = db.collection("battle_rooms").document(code)
        emitAll(
            docRef.snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(
                            BattleRoom::class.java,
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
                    if (ex != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(ex, OperationType.GET, docRef.path)
                    }
                    throw error
                }
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeParticipants(roomCode: String): Flow<List<BattleParticipant>> {
        val code = normalizeCode(roomCode)
        return observeRoom(code).flatMapLatest { room ->
            if (room == null || room.memberUids.isEmpty()) {
                flowOf(emptyList())
            } else {
                val flows = room.memberUids.map { memberUid ->
                    observeSingleParticipant(code, memberUid)
                }
                combine(flows) { participants ->
                    participants.filterNotNull()
                        .sortedWith(
                            compareByDescending<BattleParticipant> { it.score }
                                .thenByDescending { it.correctCount }
                                .thenBy { if (it.finished) it.durationSeconds else Int.MAX_VALUE }
                                .thenBy { it.displayName.lowercase() }
                        )
                }
            }
        }
    }

    private fun observeSingleParticipant(roomCode: String, userId: String): Flow<BattleParticipant?> = flow {
        val uid = requireUserId()
        val docRef = db.collection("battle_rooms")
            .document(roomCode)
            .collection("participants")
            .document(userId)

        emitAll(
            docRef.snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(
                            BattleParticipant::class.java,
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
                    if (ex != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(ex, OperationType.GET, docRef.path)
                    }
                    emit(null)
                }
        )
    }

    fun observeIncomingInvites(userId: String): Flow<List<BattleInvite>> = flow {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        val query = db.collection("battle_invites")
            .whereEqualTo("inviteeUserId", userId)
        emitAll(
            query.snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        BattleInvite::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                        .filter { it.inviteStatus == BattleInviteStatus.PENDING }
                        .sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val ex = firestoreError ?: (error as? Exception)
                    if (ex != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(ex, OperationType.LIST, "battle_invites")
                    }
                    throw error
                }
        )
    }

    fun observeRoomInvites(hostUserId: String, roomCode: String): Flow<List<BattleInvite>> = flow {
        require(hostUserId.isNotBlank()) { "Host User ID must not be blank" }
        val code = normalizeCode(roomCode)
        val query = db.collection("battle_invites")
            .whereEqualTo("hostUserId", hostUserId)
        emitAll(
            query.snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        BattleInvite::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                        .filter { it.roomCode == code }
                        .sortedByDescending { it.updatedAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val ex = firestoreError ?: (error as? Exception)
                    if (ex != null && auth.currentUser?.uid == hostUserId) {
                        handleFirestoreError(ex, OperationType.LIST, "battle_invites")
                    }
                    throw error
                }
        )
    }

    fun observeExplorersForInvite(currentUserId: String): Flow<List<LeaderboardEntry>> = flow {
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
                    ).filter { it.userId.isNotBlank() && it.userId != currentUserId }
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

    suspend fun createRoom(
        googleDisplayName: String?,
        topics: List<TriviaCategory>,
        difficulty: QuizDifficulty,
        mode: QuizMode,
        questionCount: Int,
        languageCode: String,
        initialInvitees: List<LeaderboardEntry>,
        customArticle: WikipediaArticle? = null
    ): Result<String> {
        return try {
            val uid = requireUserId()
            progressRepository.ensureProfileInitialized(uid, googleDisplayName)
            val identity = resolveUserIdentity(uid, googleDisplayName)

            val isArticleBattle = customArticle != null && mode != QuizMode.PICTURE
            val validTopics = if (isArticleBattle) {
                listOf(TriviaCategory.WIKIPEDIA)
            } else {
                topics
                    .filter { it != TriviaCategory.WIKIPEDIA }
                    .distinct()
                    .ifEmpty { listOf(TriviaCategory.GENERAL_KNOWLEDGE) }
                    .take(12)
            }
            val safeCount = questionCount.coerceIn(5, 20)
            val normalizedLang = when (languageCode.lowercase()) {
                "es" -> "es"
                "in", "id" -> "in"
                else -> "en"
            }

            val questions = if (isArticleBattle) {
                val repo = wikipediaRepository
                    ?: throw IllegalStateException("ARTICLE_QUIZ_FAILED")
                repo.generateQuizForArticleWithMeta(
                    article = customArticle!!,
                    languageCode = normalizedLang,
                    difficulty = difficulty,
                    mode = mode,
                    requestedCount = safeCount,
                    sourceMode = WikipediaRepository.SOURCE_AUTO
                ).getOrElse {
                    throw IllegalStateException("ARTICLE_QUIZ_FAILED", it)
                }.questions
            } else {
                triviaRepository.fetchBattleQuestions(
                    topics = validTopics,
                    difficulty = difficulty,
                    mode = mode,
                    count = safeCount,
                    languageCode = normalizedLang
                )
            }
            require(questions.isNotEmpty()) {
                if (isArticleBattle) "ARTICLE_QUIZ_FAILED" else "Unable to prepare questions for this battle room."
            }

            val roomCode = allocateUniqueRoomCode()
            val roomRef = db.collection("battle_rooms").document(roomCode)
            val participantRef = roomRef.collection("participants").document(uid)

            val roomPayload = mapOf(
                "roomCode" to roomCode,
                "hostUserId" to uid,
                "hostDisplayName" to identity.displayName,
                "hostAvatarId" to identity.avatarId,
                "topics" to validTopics.map { it.apiKey },
                "difficulty" to difficulty.apiKey,
                "mode" to mode.key,
                "questionCount" to questions.size,
                "languageCode" to normalizedLang,
                "status" to BattleStatus.WAITING.key,
                "memberUids" to listOf(uid),
                "questions" to questions.map { it.toBattleQuestionMap() },
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            val participantPayload = mapOf(
                "userId" to uid,
                "displayName" to identity.displayName,
                "handle" to identity.handle,
                "avatarId" to identity.avatarId,
                "score" to 0L,
                "correctCount" to 0,
                "answeredCount" to 0,
                "currentStreak" to 0,
                "bestStreak" to 0,
                "durationSeconds" to 0,
                "finished" to false,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            db.runBatch { batch ->
                batch.set(roomRef, roomPayload)
                batch.set(participantRef, participantPayload)
            }.await()

            // Dispatch any pre-selected direct friend invites right after room creation
            for (friend in initialInvitees.distinctBy { it.userId }.take(11)) {
                if (friend.userId.isBlank() || friend.userId == uid) continue
                val inviteId = "${roomCode}_${friend.userId}"
                val invitePayload = mapOf(
                    "roomId" to roomCode,
                    "roomCode" to roomCode,
                    "hostUserId" to uid,
                    "hostDisplayName" to identity.displayName,
                    "hostAvatarId" to identity.avatarId,
                    "inviteeUserId" to friend.userId,
                    "inviteeDisplayName" to friend.displayName.trim().take(30).let { if (it.length >= 2) it else "Friend" },
                    "topics" to validTopics.map { it.apiKey },
                    "difficulty" to difficulty.apiKey,
                    "mode" to mode.key,
                    "questionCount" to questions.size,
                    "status" to BattleInviteStatus.PENDING.key,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                db.collection("battle_invites").document(inviteId).set(invitePayload).await()
            }

            Result.success(roomCode)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, "battle_rooms")
            Result.failure(e)
        }
    }

    suspend fun sendDirectInvite(
        room: BattleRoom,
        invitee: LeaderboardEntry
    ): Result<Unit> {
        val inviteId = "${room.roomCode}_${invitee.userId}"
        val path = "battle_invites/$inviteId"
        return try {
            val uid = requireUserId()
            check(room.hostUserId == uid) { "Only the host can send invites." }
            check(invitee.userId.isNotBlank() && invitee.userId != uid) { "Cannot invite yourself." }
            check(room.battleStatus == BattleStatus.WAITING) { "Battle has already started." }

            val payload = mapOf(
                "roomId" to room.roomCode,
                "roomCode" to room.roomCode,
                "hostUserId" to uid,
                "hostDisplayName" to room.hostDisplayName,
                "hostAvatarId" to room.hostAvatarId,
                "inviteeUserId" to invitee.userId,
                "inviteeDisplayName" to invitee.displayName.trim().take(30).let { if (it.length >= 2) it else "Friend" },
                "topics" to room.topics,
                "difficulty" to room.difficulty,
                "mode" to room.mode,
                "questionCount" to room.questionCount,
                "status" to BattleInviteStatus.PENDING.key,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("battle_invites").document(inviteId).set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun respondToInvite(
        invite: BattleInvite,
        accept: Boolean,
        googleDisplayName: String?
    ): Result<String?> {
        val path = "battle_invites/${invite.id}"
        return try {
            val uid = requireUserId()
            check(invite.inviteeUserId == uid) { "This invitation belongs to another player." }
            val newStatus = if (accept) BattleInviteStatus.ACCEPTED.key else BattleInviteStatus.DECLINED.key
            db.collection("battle_invites").document(invite.id).update(
                mapOf(
                    "status" to newStatus,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            if (accept) {
                joinRoomByCode(invite.roomCode, googleDisplayName).map { it }
            } else {
                Result.success(null)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun joinRoomByCode(
        rawCode: String,
        googleDisplayName: String?
    ): Result<String> {
        val code = normalizeCode(rawCode)
        val path = "battle_rooms/$code"
        return try {
            require(code.length in 4..12 && code.matches(Regex("^[A-Z0-9]+$"))) {
                "ROOM_CODE_INVALID"
            }
            val uid = requireUserId()
            progressRepository.ensureProfileInitialized(uid, googleDisplayName)
            val identity = resolveUserIdentity(uid, googleDisplayName)
            val roomRef = db.collection("battle_rooms").document(code)
            val participantRef = roomRef.collection("participants").document(uid)

            db.runTransaction { tx ->
                val roomSnap = tx.get(roomRef)
                if (!roomSnap.exists()) {
                    throw IllegalStateException("ROOM_NOT_FOUND")
                }
                val status = roomSnap.getString("status") ?: BattleStatus.WAITING.key
                @Suppress("UNCHECKED_CAST")
                val members = (roomSnap.get("memberUids") as? List<String>).orEmpty()
                val participantSnap = tx.get(participantRef)

                if (uid !in members) {
                    if (status != BattleStatus.WAITING.key) {
                        throw IllegalStateException("ROOM_ALREADY_STARTED")
                    }
                    if (members.size >= 12) {
                        throw IllegalStateException("ROOM_FULL")
                    }
                    tx.update(
                        roomRef,
                        mapOf(
                            "memberUids" to (members + uid),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                }

                if (!participantSnap.exists()) {
                    tx.set(
                        participantRef,
                        mapOf(
                            "userId" to uid,
                            "displayName" to identity.displayName,
                            "handle" to identity.handle,
                            "avatarId" to identity.avatarId,
                            "score" to 0L,
                            "correctCount" to 0,
                            "answeredCount" to 0,
                            "currentStreak" to 0,
                            "bestStreak" to 0,
                            "durationSeconds" to 0,
                            "finished" to false,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                }
            }.await()

            Result.success(code)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun startBattle(roomCode: String): Result<Unit> {
        val code = normalizeCode(roomCode)
        val path = "battle_rooms/$code"
        return try {
            requireUserId()
            db.collection("battle_rooms").document(code).update(
                mapOf(
                    "status" to BattleStatus.ACTIVE.key,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun leaveWaitingRoom(room: BattleRoom): Result<Unit> {
        val path = "battle_rooms/${room.roomCode}"
        return try {
            val uid = requireUserId()
            val roomRef = db.collection("battle_rooms").document(room.roomCode)
            val participantRef = roomRef.collection("participants").document(uid)
            if (room.hostUserId == uid) {
                // Host closes waiting room
                participantRef.delete().await()
                roomRef.delete().await()
            } else {
                db.runTransaction { tx ->
                    val snap = tx.get(roomRef)
                    if (snap.exists()) {
                        @Suppress("UNCHECKED_CAST")
                        val members = (snap.get("memberUids") as? List<String>).orEmpty()
                        if (uid in members && snap.getString("status") == BattleStatus.WAITING.key) {
                            tx.update(
                                roomRef,
                                mapOf(
                                    "memberUids" to members.filterNot { it == uid },
                                    "updatedAt" to FieldValue.serverTimestamp()
                                )
                            )
                        }
                    }
                    tx.delete(participantRef)
                }.await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateParticipantProgress(
        roomCode: String,
        score: Long,
        correctCount: Int,
        answeredCount: Int,
        currentStreak: Int,
        bestStreak: Int,
        durationSeconds: Int,
        finished: Boolean
    ): Result<Unit> {
        val code = normalizeCode(roomCode)
        val uid = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("Not signed in"))
        val path = "battle_rooms/$code/participants/$uid"
        return try {
            val participantRef = db.collection("battle_rooms")
                .document(code)
                .collection("participants")
                .document(uid)

            participantRef.update(
                mapOf(
                    "score" to score.coerceIn(-50000L, 50000L),
                    "correctCount" to correctCount.coerceIn(0, 20),
                    "answeredCount" to answeredCount.coerceIn(0, 20),
                    "currentStreak" to currentStreak.coerceIn(0, 20),
                    "bestStreak" to bestStreak.coerceIn(0, 20),
                    "durationSeconds" to durationSeconds.coerceIn(0, 3600),
                    "finished" to finished,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun finishBattleRoom(roomCode: String): Result<Unit> {
        val code = normalizeCode(roomCode)
        val path = "battle_rooms/$code"
        return try {
            requireUserId()
            db.collection("battle_rooms").document(code).update(
                mapOf(
                    "status" to BattleStatus.FINISHED.key,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    private suspend fun allocateUniqueRoomCode(): String {
        val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        repeat(6) {
            val candidate = buildString {
                repeat(6) {
                    append(alphabet[Random.nextInt(alphabet.length)])
                }
            }
            val exists = db.collection("battle_rooms").document(candidate).get().await().exists()
            if (!exists) return candidate
        }
        return buildString {
            repeat(6) {
                append(alphabet[Random.nextInt(alphabet.length)])
            }
        }
    }

    private suspend fun resolveUserIdentity(uid: String, fallbackName: String?): UserIdentity {
        val profileSnap = runCatching {
            db.collection("users").document(uid).get().await()
        }.getOrNull()
        val rawName = profileSnap?.getString("displayName") ?: fallbackName ?: "Atlas Explorer"
        val cleanName = rawName.trim().take(30).let { if (it.length >= 2) it else "Atlas Explorer" }
        val rawHandle = profileSnap?.getString("handle") ?: "explorer_${uid.take(4).lowercase()}"
        val cleanHandle = rawHandle.trim().take(24).let { if (it.length >= 2) it else "explorer" }
        val avatarId = AvatarOption.fromId(profileSnap?.getString("avatarId")).id
        return UserIdentity(cleanName, cleanHandle, avatarId)
    }

    fun normalizeCode(raw: String): String =
        raw.trim().uppercase().replace(Regex("[^A-Z0-9]"), "")

    private data class UserIdentity(
        val displayName: String,
        val handle: String,
        val avatarId: String
    )
}
