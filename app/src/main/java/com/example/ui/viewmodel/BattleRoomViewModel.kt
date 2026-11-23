package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.BattleRepository
import com.example.data.firebase.UserProgressRepository
import com.example.data.local.ThemePreferences
import com.example.data.model.AnswerMatcher
import com.example.data.model.BattleInvite
import com.example.data.model.BattleParticipant
import com.example.data.model.BattleRoom
import com.example.data.model.BattleStatus
import com.example.data.model.LeaderboardEntry
import com.example.data.model.QuestionReviewItem
import com.example.data.model.QuizDifficulty
import com.example.data.model.QuizMode
import com.example.data.model.QuizRoundResult
import com.example.data.model.TriviaCategory
import com.example.data.model.TriviaQuestion
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BattlePlayState(
    val questions: List<TriviaQuestion>,
    val currentIndex: Int,
    val selectedOption: String?,
    val guessInput: String,
    val hintRevealed: Boolean,
    val hintPattern: String,
    val revealed: Boolean,
    val lastAnswerCorrect: Boolean,
    val timedOut: Boolean,
    val secondsRemaining: Int,
    val maxSeconds: Int,
    val timerEnabled: Boolean,
    val score: Long,
    val correctCount: Int,
    val currentStreak: Int,
    val bestStreak: Int,
    val reviewItems: List<QuestionReviewItem>,
    val completed: Boolean
) {
    val currentQuestion: TriviaQuestion get() = questions[currentIndex]
    val isLastQuestion: Boolean get() = currentIndex >= questions.lastIndex
}

object BattleScoring {
    const val BATTLE_QUESTION_SECONDS = 30

    fun wrongPenalty(difficulty: QuizDifficulty): Long = when (difficulty) {
        QuizDifficulty.EASY -> 40L
        QuizDifficulty.MEDIUM, QuizDifficulty.ALL -> 50L
        QuizDifficulty.HARD -> 65L
    }

    fun calculateXpDelta(
        isCorrect: Boolean,
        hasAnswer: Boolean,
        difficulty: QuizDifficulty,
        mode: QuizMode,
        hintRevealed: Boolean,
        secondsRemaining: Int,
        maxSeconds: Int = BATTLE_QUESTION_SECONDS,
        newStreak: Int = 1
    ): Long {
        if (!hasAnswer) return 0L
        if (!isCorrect) return -wrongPenalty(difficulty)

        val base = when (difficulty) {
            QuizDifficulty.EASY -> 80L
            QuizDifficulty.MEDIUM, QuizDifficulty.ALL -> 100L
            QuizDifficulty.HARD -> 130L
        }
        val modeBase = if (mode == QuizMode.GUESS && !hintRevealed) {
            (base * 3L) / 2L
        } else {
            base
        }
        val safeMax = maxSeconds.coerceAtLeast(1)
        val clampedRemaining = secondsRemaining.coerceIn(0, safeMax)
        val speedBonus = ((clampedRemaining.toFloat() / safeMax.toFloat()) * 80f)
            .toLong()
            .coerceIn(0L, 80L)
        val streakBonus = minOf(newStreak, 5) * 15L
        return modeBase + speedBonus + streakBonus
    }
}

class BattleRoomViewModel(
    val roomCode: String,
    val currentUserId: String?,
    private val googleDisplayName: String?,
    private val battleRepository: BattleRepository,
    private val progressRepository: UserProgressRepository,
    private val themePreferences: ThemePreferences
) : ViewModel() {

    val roomState: StateFlow<UiState<BattleRoom?>> = if (currentUserId != null) {
        battleRepository.observeRoom(roomCode)
            .map<BattleRoom?, UiState<BattleRoom?>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing battle room $roomCode", rootError)
                emit(UiState.Error(rootError.localizedMessage ?: "Failed to load battle room"))
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)
    } else {
        flowOf<UiState<BattleRoom?>>(UiState.Error("Sign in required"))
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Error("Sign in required"))
    }

    val participants: StateFlow<List<BattleParticipant>> = if (currentUserId != null) {
        battleRepository.observeParticipants(roomCode)
            .catch { error ->
                if (error is CancellationException) throw error
                Log.w(TAG, "Error observing participants", error)
                emit(emptyList())
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())
    } else {
        flowOf(emptyList<BattleParticipant>())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())
    }

    val explorers: StateFlow<List<LeaderboardEntry>> = if (currentUserId != null) {
        battleRepository.observeExplorersForInvite(currentUserId)
            .catch { error ->
                if (error is CancellationException) throw error
                emit(emptyList())
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())
    } else {
        flowOf(emptyList<LeaderboardEntry>())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())
    }

    val roomInvites: StateFlow<List<BattleInvite>> = if (currentUserId != null) {
        battleRepository.observeRoomInvites(currentUserId, roomCode)
            .catch { error ->
                if (error is CancellationException) throw error
                emit(emptyList())
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())
    } else {
        flowOf(emptyList<BattleInvite>())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())
    }

    private val _playState = MutableStateFlow<BattlePlayState?>(null)
    val playState: StateFlow<BattlePlayState?> = _playState.asStateFlow()

    private val _actionBusy = MutableStateFlow(false)
    val actionBusy: StateFlow<Boolean> = _actionBusy.asStateFlow()

    private val _invitingUserId = MutableStateFlow<String?>(null)
    val invitingUserId: StateFlow<String?> = _invitingUserId.asStateFlow()

    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    private var timerJob: Job? = null
    private var playInitialized = false
    private var roundStartMillis: Long = System.currentTimeMillis()
    private var syncedToPersonalProfile = false
    private var markedRoomFinished = false

    init {
        if (currentUserId != null) {
            observeRoomStartAndAutoFinish()
        }
    }

    private fun observeRoomStartAndAutoFinish() {
        viewModelScope.launch {
            roomState.collectLatest { state ->
                val room = (state as? UiState.Success)?.data ?: return@collectLatest
                if (!playInitialized &&
                    (room.battleStatus == BattleStatus.ACTIVE || room.battleStatus == BattleStatus.FINISHED) &&
                    room.questions.isNotEmpty()
                ) {
                    val currentParticipants = participants.value
                    val mine = currentParticipants.firstOrNull { it.userId == currentUserId }
                    initializeArenaForRoom(room, alreadyFinishedRemotely = mine?.finished == true || room.battleStatus == BattleStatus.FINISHED)
                }
            }
        }
        viewModelScope.launch {
            participants.collectLatest { list ->
                val room = (roomState.value as? UiState.Success)?.data ?: return@collectLatest
                if (room.battleStatus == BattleStatus.ACTIVE &&
                    !markedRoomFinished &&
                    room.memberUids.isNotEmpty() &&
                    list.size >= room.memberUids.size &&
                    list.all { it.finished }
                ) {
                    markedRoomFinished = true
                    battleRepository.finishBattleRoom(room.roomCode)
                }
            }
        }
    }

    private fun initializeArenaForRoom(
        room: BattleRoom,
        alreadyFinishedRemotely: Boolean
    ) {
        if (playInitialized) return
        playInitialized = true
        val questions = room.questions.map { it.toTriviaQuestion() }
        if (questions.isEmpty()) return

        // Battle Rule 5: All questions have a strict 30s timer; faster answers earn more XP
        val timerEnabled = true
        val maxSec = BattleScoring.BATTLE_QUESTION_SECONDS
        val mine = participants.value.firstOrNull { it.userId == currentUserId }

        if (alreadyFinishedRemotely) {
            val review = questions.mapIndexed { idx, q ->
                QuestionReviewItem(
                    index = idx + 1,
                    question = q,
                    userAnswer = null,
                    wasCorrect = false,
                    timedOut = false,
                    usedHint = false,
                    pointsEarned = 0L,
                    explanation = q.explanation ?: q.correctAnswer
                )
            }
            _playState.value = BattlePlayState(
                questions = questions,
                currentIndex = questions.lastIndex,
                selectedOption = null,
                guessInput = "",
                hintRevealed = false,
                hintPattern = AnswerMatcher.buildHintPattern(questions.first().correctAnswer),
                revealed = true,
                lastAnswerCorrect = false,
                timedOut = false,
                secondsRemaining = 0,
                maxSeconds = maxSec,
                timerEnabled = false,
                score = mine?.score ?: 0L,
                correctCount = mine?.correctCount ?: 0,
                currentStreak = mine?.currentStreak ?: 0,
                bestStreak = mine?.bestStreak ?: 0,
                reviewItems = review,
                completed = true
            )
            return
        }

        roundStartMillis = System.currentTimeMillis()
        val firstQ = questions.first()
        _playState.value = BattlePlayState(
            questions = questions,
            currentIndex = 0,
            selectedOption = null,
            guessInput = "",
            hintRevealed = false,
            hintPattern = AnswerMatcher.buildHintPattern(firstQ.correctAnswer),
            revealed = false,
            lastAnswerCorrect = false,
            timedOut = false,
            secondsRemaining = maxSec,
            maxSeconds = maxSec,
            timerEnabled = timerEnabled,
            score = 0L,
            correctCount = 0,
            currentStreak = 0,
            bestStreak = 0,
            reviewItems = emptyList(),
            completed = false
        )
        launchTimer()
    }

    fun sendDirectInvite(friend: LeaderboardEntry) {
        val room = (roomState.value as? UiState.Success)?.data ?: return
        if (currentUserId == null || _invitingUserId.value != null) return
        viewModelScope.launch {
            _invitingUserId.value = friend.userId
            val res = battleRepository.sendDirectInvite(room, friend)
            _invitingUserId.value = null
            if (res.isFailure) {
                _feedbackMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Failed to send invite"
            }
        }
    }

    fun startBattle() {
        val room = (roomState.value as? UiState.Success)?.data ?: return
        if (currentUserId != room.hostUserId || _actionBusy.value) return
        viewModelScope.launch {
            _actionBusy.value = true
            val res = battleRepository.startBattle(room.roomCode)
            _actionBusy.value = false
            if (res.isFailure) {
                _feedbackMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Unable to start battle"
            }
        }
    }

    fun leaveWaitingRoom(onLeft: () -> Unit) {
        val room = (roomState.value as? UiState.Success)?.data
        if (room == null) {
            onLeft()
            return
        }
        viewModelScope.launch {
            _actionBusy.value = true
            battleRepository.leaveWaitingRoom(room)
            _actionBusy.value = false
            onLeft()
        }
    }

    fun markRoomFinished() {
        val room = (roomState.value as? UiState.Success)?.data ?: return
        if (_actionBusy.value) return
        viewModelScope.launch {
            _actionBusy.value = true
            battleRepository.finishBattleRoom(room.roomCode)
            _actionBusy.value = false
        }
    }

    fun selectAnswer(option: String) {
        val current = _playState.value ?: return
        if (current.completed || current.revealed) return
        val isCorrect = option == current.currentQuestion.correctAnswer
        resolveAnswer(current, submitted = option, isCorrect = isCorrect, timedOut = false)
    }

    fun updateGuessInput(input: String) {
        val current = _playState.value ?: return
        if (current.completed || current.revealed) return
        _playState.value = current.copy(guessInput = input)
    }

    fun revealHint() {
        val current = _playState.value ?: return
        if (current.completed || current.revealed || current.hintRevealed) return
        _playState.value = current.copy(hintRevealed = true)
    }

    fun submitGuess() {
        val current = _playState.value ?: return
        if (current.completed || current.revealed) return
        val guess = current.guessInput.trim()
        if (guess.isEmpty()) return
        val isCorrect = AnswerMatcher.isGuessMatch(guess, current.currentQuestion.correctAnswer)
        resolveAnswer(current, submitted = guess, isCorrect = isCorrect, timedOut = false)
    }

    fun skipGuess() {
        val current = _playState.value ?: return
        if (current.completed || current.revealed) return
        // Battle Rule 3: Passing/skipping without submitting = No answer -> 0 XP
        resolveAnswer(
            current = current,
            submitted = null,
            isCorrect = false,
            timedOut = false
        )
    }

    private fun resolveAnswer(
        current: BattlePlayState,
        submitted: String?,
        isCorrect: Boolean,
        timedOut: Boolean
    ) {
        timerJob?.cancel()
        val room = (roomState.value as? UiState.Success)?.data
        val roomMode = room?.quizMode ?: QuizMode.STANDARD
        val newStreak = if (isCorrect) current.currentStreak + 1 else 0
        val newBestStreak = maxOf(current.bestStreak, newStreak)

        val hasAnswer = !timedOut && !submitted.isNullOrBlank()
        val xpDelta = BattleScoring.calculateXpDelta(
            isCorrect = isCorrect,
            hasAnswer = hasAnswer,
            difficulty = current.currentQuestion.difficulty,
            mode = roomMode,
            hintRevealed = current.hintRevealed,
            secondsRemaining = current.secondsRemaining,
            maxSeconds = current.maxSeconds,
            newStreak = newStreak
        )

        val newScore = current.score + xpDelta
        val newCorrect = current.correctCount + if (isCorrect) 1 else 0
        val answeredCount = current.currentIndex + 1

        val reviewItem = QuestionReviewItem(
            index = answeredCount,
            question = current.currentQuestion,
            userAnswer = submitted,
            wasCorrect = isCorrect,
            timedOut = timedOut,
            usedHint = current.hintRevealed,
            pointsEarned = xpDelta,
            explanation = current.currentQuestion.explanation ?: current.currentQuestion.correctAnswer
        )

        val updated = current.copy(
            selectedOption = submitted,
            revealed = true,
            lastAnswerCorrect = isCorrect,
            timedOut = timedOut,
            score = newScore,
            correctCount = newCorrect,
            currentStreak = newStreak,
            bestStreak = newBestStreak,
            reviewItems = current.reviewItems + reviewItem
        )
        _playState.value = updated

        val elapsedSec = ((System.currentTimeMillis() - roundStartMillis) / 1000L)
            .toInt()
            .coerceIn(0, 3600)

        viewModelScope.launch {
            battleRepository.updateParticipantProgress(
                roomCode = roomCode,
                score = newScore,
                correctCount = newCorrect,
                answeredCount = answeredCount,
                currentStreak = newStreak,
                bestStreak = newBestStreak,
                durationSeconds = elapsedSec,
                finished = false
            )
        }
    }

    fun advanceToNext() {
        val current = _playState.value ?: return
        if (!current.revealed || current.completed) return

        if (current.isLastQuestion) {
            finishLocalBattle(current)
        } else {
            timerJob?.cancel()
            val nextIdx = current.currentIndex + 1
            val nextQ = current.questions[nextIdx]
            _playState.value = current.copy(
                currentIndex = nextIdx,
                selectedOption = null,
                guessInput = "",
                hintRevealed = false,
                hintPattern = AnswerMatcher.buildHintPattern(nextQ.correctAnswer),
                revealed = false,
                lastAnswerCorrect = false,
                timedOut = false,
                secondsRemaining = current.maxSeconds
            )
            if (current.timerEnabled) {
                launchTimer()
            }
        }
    }

    private fun finishLocalBattle(finalPlay: BattlePlayState) {
        timerJob?.cancel()
        val elapsedSec = ((System.currentTimeMillis() - roundStartMillis) / 1000L)
            .toInt()
            .coerceIn(1, 3600)

        _playState.value = finalPlay.copy(completed = true)

        val room = (roomState.value as? UiState.Success)?.data
        val uid = currentUserId ?: return

        viewModelScope.launch {
            battleRepository.updateParticipantProgress(
                roomCode = roomCode,
                score = finalPlay.score,
                correctCount = finalPlay.correctCount,
                answeredCount = finalPlay.questions.size,
                currentStreak = finalPlay.currentStreak,
                bestStreak = finalPlay.bestStreak,
                durationSeconds = elapsedSec,
                finished = true
            )

            if (!syncedToPersonalProfile && finalPlay.questions.isNotEmpty()) {
                syncedToPersonalProfile = true
                val primaryCategory = room?.topicCategories?.firstOrNull() ?: TriviaCategory.MIXED
                val result = QuizRoundResult(
                    category = primaryCategory,
                    difficulty = room?.quizDifficulty ?: QuizDifficulty.ALL,
                    mode = room?.quizMode ?: QuizMode.STANDARD,
                    score = maxOf(0L, finalPlay.score),
                    correctCount = finalPlay.correctCount,
                    totalQuestions = finalPlay.questions.size,
                    bestStreak = finalPlay.bestStreak,
                    durationSeconds = elapsedSec,
                    playedOffline = false
                )
                progressRepository.recordQuizResult(uid, result, googleDisplayName)
            }
        }
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    private fun launchTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val current = _playState.value ?: break
                if (current.completed || current.revealed) break
                val nextSec = current.secondsRemaining - 1
                if (nextSec <= 0) {
                    resolveAnswer(
                        current = current.copy(secondsRemaining = 0),
                        submitted = null,
                        isCorrect = false,
                        timedOut = true
                    )
                    break
                } else {
                    _playState.value = current.copy(secondsRemaining = nextSec)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    private companion object {
        const val TAG = "BattleRoomVM"
    }
}
