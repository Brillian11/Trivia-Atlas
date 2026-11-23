package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.UserProgressRepository
import com.example.data.local.ThemePreferences
import com.example.data.model.AnswerMatcher
import com.example.data.model.AppLanguage
import com.example.data.model.ProgressionSystem
import com.example.data.model.QuestionReviewItem
import com.example.data.model.QuizDifficulty
import com.example.data.model.QuizMode
import com.example.data.model.QuizRoundResult
import com.example.data.model.TriviaCategory
import com.example.data.model.TriviaQuestion
import com.example.data.remote.NetworkMonitor
import com.example.data.repository.TriviaRepository
import com.example.data.repository.WikipediaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface QuizScreenState {
    data object Loading : QuizScreenState
    data class Error(val message: String) : QuizScreenState
    data class Active(
        val mode: QuizMode,
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
        val servedFromOfflineCache: Boolean,
        val reviewItems: List<QuestionReviewItem>,
        val articleTitle: String? = null,
        val wikiAiModelName: String? = null,
        val wikiAutoFallbackToInstant: Boolean = false
    ) : QuizScreenState {
        val currentQuestion: TriviaQuestion get() = questions[currentIndex]
        val isLastQuestion: Boolean get() = currentIndex >= questions.lastIndex
    }

    data class Completed(
        val result: QuizRoundResult,
        val reviewItems: List<QuestionReviewItem>,
        val syncedToCloud: Boolean,
        val queuedOffline: Boolean,
        val isGuest: Boolean = false,
        val syncError: String? = null,
        val articleTitle: String? = null,
        val freezesEarned: Int = 0,
        val leveledUp: Boolean = false,
        val newlyCompletedObjectivesCount: Int = 0
    ) : QuizScreenState
}

class QuizViewModel(
    private val currentUserId: String?,
    private val googleDisplayName: String?,
    private val category: TriviaCategory,
    private val difficulty: QuizDifficulty,
    private val mode: QuizMode,
    private val questionCount: Int,
    private val languageCode: String,
    private val triviaRepository: TriviaRepository,
    private val progressRepository: UserProgressRepository,
    private val networkMonitor: NetworkMonitor,
    private val themePreferences: ThemePreferences,
    private val wikiPageId: Long? = null,
    private val wikiLang: String? = null,
    val wikiArticleTitle: String? = null,
    private val wikiSourceMode: String = WikipediaRepository.SOURCE_AUTO,
    private val wikipediaRepository: WikipediaRepository? = null
) : ViewModel() {

    val isWikipediaQuiz: Boolean = wikiPageId != null

    private val _state = MutableStateFlow<QuizScreenState>(QuizScreenState.Loading)
    val state: StateFlow<QuizScreenState> = _state.asStateFlow()

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), true)

    private var timerJob: Job? = null
    private var roundStartMillis: Long = System.currentTimeMillis()

    init {
        startRound()
    }

    fun startLocalWikiFallback() {
        launchRound(overrideWikiSource = WikipediaRepository.SOURCE_LOCAL)
    }

    fun startRound() {
        launchRound(overrideWikiSource = null)
    }

    private fun launchRound(overrideWikiSource: String?) {
        timerJob?.cancel()
        _state.value = QuizScreenState.Loading
        roundStartMillis = System.currentTimeMillis()
        viewModelScope.launch {
            val timerEnabled = themePreferences.timerEnabled.first()
            val savedLanguage = themePreferences.appLanguage.first()
            val effectiveLanguage = if (savedLanguage == AppLanguage.SYSTEM) {
                languageCode
            } else {
                savedLanguage.code
            }

            val questions: List<TriviaQuestion>
            val servedFromOfflineCache: Boolean
            var resolvedArticleTitle: String? = wikiArticleTitle
            var wikiAiModelName: String? = null
            var wikiAutoFallbackToInstant = false

            if (wikiPageId != null && wikipediaRepository != null) {
                val langForLookup = wikiLang ?: "en"
                val article = wikipediaRepository.findArticle(
                    currentUserId = currentUserId,
                    wikiLang = langForLookup,
                    pageId = wikiPageId
                )
                if (article == null) {
                    _state.value = QuizScreenState.Error("ARTICLE_NOT_FOUND")
                    return@launch
                }
                resolvedArticleTitle = article.title
                val aiResult = wikipediaRepository.generateQuizForArticleWithMeta(
                    article = article,
                    languageCode = effectiveLanguage,
                    difficulty = difficulty,
                    mode = mode,
                    requestedCount = questionCount,
                    sourceMode = overrideWikiSource ?: wikiSourceMode
                )
                if (aiResult.isFailure) {
                    val err = aiResult.exceptionOrNull()?.localizedMessage
                        ?: "Failed to generate trivia quiz from Wikipedia article."
                    _state.value = QuizScreenState.Error(err)
                    return@launch
                }
                val metaResult = aiResult.getOrThrow()
                questions = metaResult.questions
                wikiAiModelName = metaResult.aiModelDisplayName
                wikiAutoFallbackToInstant = metaResult.fellBackToInstant
                servedFromOfflineCache = false
            } else {
                val bundle = triviaRepository.fetchSessionQuestions(
                    category = category,
                    difficulty = difficulty,
                    mode = mode,
                    questionCount = questionCount,
                    languageCode = effectiveLanguage
                )
                questions = bundle.questions
                servedFromOfflineCache = bundle.servedFromOfflineCache
                if (questions.isEmpty()) {
                    _state.value = QuizScreenState.Error(bundle.errorCode ?: "NO_QUESTIONS")
                    return@launch
                }
            }

            if (questions.isEmpty()) {
                _state.value = QuizScreenState.Error("NO_QUESTIONS")
                return@launch
            }
            val maxSec = when (mode) {
                QuizMode.GUESS -> 30
                QuizMode.PICTURE -> 25
                QuizMode.STANDARD -> 20
            }
            val firstQuestion = questions.first()
            _state.value = QuizScreenState.Active(
                mode = mode,
                questions = questions,
                currentIndex = 0,
                selectedOption = null,
                guessInput = "",
                hintRevealed = false,
                hintPattern = AnswerMatcher.buildHintPattern(firstQuestion.correctAnswer),
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
                servedFromOfflineCache = servedFromOfflineCache,
                reviewItems = emptyList(),
                articleTitle = resolvedArticleTitle,
                wikiAiModelName = wikiAiModelName,
                wikiAutoFallbackToInstant = wikiAutoFallbackToInstant
            )
            if (timerEnabled) {
                launchQuestionTimer()
            }
        }
    }

    fun updateGuessInput(input: String) {
        val current = _state.value as? QuizScreenState.Active ?: return
        if (current.revealed) return
        _state.value = current.copy(guessInput = input)
    }

    fun revealHint() {
        val current = _state.value as? QuizScreenState.Active ?: return
        if (current.revealed || current.hintRevealed) return
        _state.value = current.copy(hintRevealed = true)
    }

    fun submitGuess() {
        val current = _state.value as? QuizScreenState.Active ?: return
        if (current.revealed) return
        val guess = current.guessInput.trim()
        if (guess.isEmpty()) return

        val isCorrect = AnswerMatcher.isGuessMatch(guess, current.currentQuestion.correctAnswer)
        resolveQuestionAnswer(
            current = current,
            submittedAnswer = guess,
            isCorrect = isCorrect,
            timedOut = false
        )
    }

    fun skipGuess() {
        val current = _state.value as? QuizScreenState.Active ?: return
        if (current.revealed) return
        resolveQuestionAnswer(
            current = current,
            submittedAnswer = current.guessInput.trim().takeIf { it.isNotEmpty() },
            isCorrect = false,
            timedOut = false
        )
    }

    fun selectAnswer(option: String) {
        val current = _state.value as? QuizScreenState.Active ?: return
        if (current.revealed) return
        val isCorrect = option == current.currentQuestion.correctAnswer
        resolveQuestionAnswer(
            current = current,
            submittedAnswer = option,
            isCorrect = isCorrect,
            timedOut = false
        )
    }

    private fun resolveQuestionAnswer(
        current: QuizScreenState.Active,
        submittedAnswer: String?,
        isCorrect: Boolean,
        timedOut: Boolean
    ) {
        timerJob?.cancel()
        val newStreak = if (isCorrect) current.currentStreak + 1 else 0
        val newBestStreak = maxOf(current.bestStreak, newStreak)

        val earnedPoints = if (isCorrect) {
            val base = when (current.currentQuestion.difficulty) {
                QuizDifficulty.EASY -> 80L
                QuizDifficulty.MEDIUM -> 100L
                QuizDifficulty.HARD -> 130L
                QuizDifficulty.ALL -> 100L
            }
            val modeAdjustedBase = if (current.mode == QuizMode.GUESS && !current.hintRevealed) {
                (base * 3L) / 2L
            } else {
                base
            }
            val speedBonus = if (current.timerEnabled) {
                ((current.secondsRemaining.toFloat() / current.maxSeconds.toFloat()) * 30f).toLong().coerceAtLeast(0L)
            } else 10L
            val streakBonus = (minOf(newStreak, 5) * 15L)
            modeAdjustedBase + speedBonus + streakBonus
        } else {
            0L
        }

        val reviewItem = QuestionReviewItem(
            index = current.currentIndex + 1,
            question = current.currentQuestion,
            userAnswer = submittedAnswer,
            wasCorrect = isCorrect,
            timedOut = timedOut,
            usedHint = current.hintRevealed,
            pointsEarned = earnedPoints,
            explanation = current.currentQuestion.explanation
                ?: current.currentQuestion.correctAnswer
        )

        _state.value = current.copy(
            selectedOption = submittedAnswer,
            revealed = true,
            lastAnswerCorrect = isCorrect,
            timedOut = timedOut,
            score = current.score + earnedPoints,
            correctCount = current.correctCount + if (isCorrect) 1 else 0,
            currentStreak = newStreak,
            bestStreak = newBestStreak,
            reviewItems = current.reviewItems + reviewItem
        )
    }

    fun advanceToNext() {
        val current = _state.value as? QuizScreenState.Active ?: return
        if (!current.revealed) return

        if (current.isLastQuestion) {
            finishRound(current)
        } else {
            timerJob?.cancel()
            val nextIndex = current.currentIndex + 1
            val nextQuestion = current.questions[nextIndex]
            _state.value = current.copy(
                currentIndex = nextIndex,
                selectedOption = null,
                guessInput = "",
                hintRevealed = false,
                hintPattern = AnswerMatcher.buildHintPattern(nextQuestion.correctAnswer),
                revealed = false,
                lastAnswerCorrect = false,
                timedOut = false,
                secondsRemaining = current.maxSeconds
            )
            if (current.timerEnabled) {
                launchQuestionTimer()
            }
        }
    }

    private fun finishRound(finalState: QuizScreenState.Active) {
        timerJob?.cancel()
        val elapsedSec = ((System.currentTimeMillis() - roundStartMillis) / 1000L)
            .toInt()
            .coerceIn(1, 3600)

        val roundResult = QuizRoundResult(
            category = category,
            difficulty = difficulty,
            mode = mode,
            score = finalState.score,
            correctCount = finalState.correctCount,
            totalQuestions = finalState.questions.size,
            bestStreak = finalState.bestStreak,
            durationSeconds = elapsedSec,
            playedOffline = finalState.servedFromOfflineCache || !networkMonitor.checkCurrentConnectivity()
        )

        val hintsUsed = finalState.reviewItems.count { it.usedHint }

        viewModelScope.launch {
            val uid = currentUserId
            if (uid == null) {
                val prevGuestScore = triviaRepository.observeGuestStats().first().totalScore
                triviaRepository.saveGuestSession(roundResult)
                val reward = themePreferences.recordGuestXpForStreakFreezes(
                    earnedXp = roundResult.score,
                    prevLifetimeXp = prevGuestScore,
                    completedRound = roundResult,
                    hintsUsed = hintsUsed
                )
                val leveledUp = ProgressionSystem.calculateLevelProgress(prevGuestScore + roundResult.score).level >
                    ProgressionSystem.calculateLevelProgress(prevGuestScore).level
                _state.value = QuizScreenState.Completed(
                    result = roundResult,
                    reviewItems = finalState.reviewItems,
                    syncedToCloud = false,
                    queuedOffline = false,
                    isGuest = true,
                    articleTitle = finalState.articleTitle,
                    freezesEarned = reward.totalFreezesEarned,
                    leveledUp = leveledUp,
                    newlyCompletedObjectivesCount = reward.newlyCompletedObjectiveIds.size
                )
                return@launch
            }

            if (networkMonitor.checkCurrentConnectivity()) {
                val res = progressRepository.recordQuizResult(
                    userId = uid,
                    result = roundResult,
                    fallbackDisplayName = googleDisplayName,
                    hintsUsed = hintsUsed
                )
                if (res.isSuccess) {
                    val reward = res.getOrNull()
                    _state.value = QuizScreenState.Completed(
                        result = roundResult,
                        reviewItems = finalState.reviewItems,
                        syncedToCloud = true,
                        queuedOffline = false,
                        isGuest = false,
                        articleTitle = finalState.articleTitle,
                        freezesEarned = reward?.totalFreezesEarned ?: 0,
                        leveledUp = (reward?.freezesEarnedFromLevelUp ?: 0) > 0,
                        newlyCompletedObjectivesCount = reward?.newlyCompletedObjectiveIds?.size ?: 0
                    )
                } else {
                    triviaRepository.queueOfflineScore(uid, roundResult)
                    _state.value = QuizScreenState.Completed(
                        result = roundResult,
                        reviewItems = finalState.reviewItems,
                        syncedToCloud = false,
                        queuedOffline = true,
                        isGuest = false,
                        syncError = res.exceptionOrNull()?.localizedMessage,
                        articleTitle = finalState.articleTitle
                    )
                }
            } else {
                triviaRepository.queueOfflineScore(uid, roundResult)
                _state.value = QuizScreenState.Completed(
                    result = roundResult,
                    reviewItems = finalState.reviewItems,
                    syncedToCloud = false,
                    queuedOffline = true,
                    isGuest = false,
                    articleTitle = finalState.articleTitle
                )
            }
        }
    }

    private fun launchQuestionTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val current = _state.value as? QuizScreenState.Active ?: break
                if (current.revealed) break
                val nextSec = current.secondsRemaining - 1
                if (nextSec <= 0) {
                    resolveQuestionAnswer(
                        current = current.copy(secondsRemaining = 0),
                        submittedAnswer = null,
                        isCorrect = false,
                        timedOut = true
                    )
                    break
                } else {
                    _state.value = current.copy(secondsRemaining = nextSec)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
