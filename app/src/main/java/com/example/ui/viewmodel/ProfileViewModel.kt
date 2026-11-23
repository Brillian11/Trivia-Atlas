package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.UserProgressRepository
import com.example.data.local.ThemePreferences
import com.example.data.model.AppLanguage
import com.example.data.model.AvatarOption
import com.example.data.model.GeminiAvailableModel
import com.example.data.model.GuestStatsSummary
import com.example.data.model.ProgressionSystem
import com.example.data.model.QuizDifficulty
import com.example.data.model.QuizMode
import com.example.data.model.QuizSession
import com.example.data.model.ThemeMode
import com.example.data.model.TriviaCategory
import com.example.data.model.UserProfile
import com.example.data.model.WeeklyRankingCycle
import com.example.data.model.WeeklyRankingEntry
import com.example.data.remote.NetworkMonitor
import com.example.data.repository.TriviaRepository
import com.example.data.repository.WikipediaRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val currentUserId: String?,
    private val progressRepository: UserProgressRepository,
    private val triviaRepository: TriviaRepository,
    private val wikipediaRepository: WikipediaRepository,
    networkMonitor: NetworkMonitor,
    private val themePreferences: ThemePreferences
) : ViewModel() {

    val isGuest: Boolean = currentUserId == null

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline

    val guestStats: StateFlow<GuestStatsSummary> = triviaRepository.observeGuestStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), GuestStatsSummary())

    private val clockFlow: Flow<Long> = flow {
        while (currentCoroutineContext().isActive) {
            emit(System.currentTimeMillis())
            delay(15_000L)
        }
    }

    val weeklyCycle: StateFlow<WeeklyRankingCycle> = clockFlow
        .map { ProgressionSystem.currentWeeklyCycle(it) }
        .distinctUntilChanged { old, new -> old.cycleId == new.cycleId }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ProgressionSystem.currentWeeklyCycle()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val weeklyRankingState: StateFlow<WeeklyRankingEntry?> = if (currentUserId != null) {
        weeklyCycle
            .map { it.cycleId }
            .distinctUntilChanged()
            .flatMapLatest { cycleId ->
                progressRepository.observeUserWeeklyRanking(currentUserId, cycleId)
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                Log.w(TAG, "Error observing user weekly ranking", firestoreError ?: error)
                emit(null)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)
    } else {
        flowOf<WeeklyRankingEntry?>(null)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)
    }

    val profileState: StateFlow<UiState<UserProfile?>> = if (currentUserId != null) {
        progressRepository.observeUserProfile(currentUserId)
            .map<UserProfile?, UiState<UserProfile?>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing user profile", rootError)
                emit(UiState.Error(rootError.localizedMessage ?: "Failed to load profile"))
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)
    } else {
        flowOf<UiState<UserProfile?>>(UiState.Success(null))
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Success(null))
    }

    val streakSnapshot: StateFlow<ProgressionSystem.StreakSnapshot> = if (currentUserId != null) {
        profileState
            .map { state ->
                (state as? UiState.Success)?.data?.toStreakSnapshot()
                    ?: ProgressionSystem.StreakSnapshot()
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), ProgressionSystem.StreakSnapshot())
    } else {
        themePreferences.guestStreak
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), ProgressionSystem.StreakSnapshot())
    }

    val sessionsState: StateFlow<UiState<List<QuizSession>>> = if (currentUserId != null) {
        progressRepository.observeQuizSessions(currentUserId)
            .map<List<QuizSession>, UiState<List<QuizSession>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing quiz sessions", rootError)
                emit(UiState.Error(rootError.localizedMessage ?: "Failed to load quiz history"))
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)
    } else {
        triviaRepository.observeGuestSessions()
            .map<List<QuizSession>, UiState<List<QuizSession>>> { UiState.Success(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)
    }

    val themeMode: StateFlow<ThemeMode> = themePreferences.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), ThemeMode.SYSTEM)

    val appLanguage: StateFlow<AppLanguage> = themePreferences.appLanguage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), AppLanguage.SYSTEM)

    val timerEnabled: StateFlow<Boolean> = themePreferences.timerEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), true)

    val selectedPlayMode: StateFlow<QuizMode> = themePreferences.playMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), QuizMode.STANDARD)

    val selectedQuestionCount: StateFlow<Int> = themePreferences.questionCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), 10)

    val selectedDifficulty: StateFlow<QuizDifficulty> = themePreferences.difficulty
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), QuizDifficulty.ALL)

    val offlineModeEnabled: StateFlow<Boolean> = themePreferences.offlineModeEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)

    val totalCachedCount: StateFlow<Int> = triviaRepository.totalCachedCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), 0)

    val customGeminiApiKey: StateFlow<String?> = themePreferences.customGeminiApiKey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    val customGeminiModelId: StateFlow<String?> = themePreferences.customGeminiModelId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    val customGeminiModelName: StateFlow<String?> = themePreferences.customGeminiModelName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    private val _availableGeminiModelsState =
        MutableStateFlow<UiState<List<GeminiAvailableModel>>>(UiState.Success(emptyList()))
    val availableGeminiModelsState: StateFlow<UiState<List<GeminiAvailableModel>>> =
        _availableGeminiModelsState.asStateFlow()

    val pendingScoreCount: StateFlow<Int> = if (currentUserId != null) {
        triviaRepository.observePendingScoreCount(currentUserId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), 0)
    } else {
        MutableStateFlow(0).asStateFlow()
    }

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    init {
        viewModelScope.launch {
            themePreferences.customGeminiApiKey
                .map { it?.trim().orEmpty() }
                .distinctUntilChanged()
                .collectLatest { key ->
                    if (key.isBlank()) {
                        _availableGeminiModelsState.value = UiState.Success(emptyList())
                    } else {
                        loadAvailableModels(key)
                    }
                }
        }
    }

    private suspend fun loadAvailableModels(apiKey: String) {
        _availableGeminiModelsState.value = UiState.Loading
        val result = wikipediaRepository.fetchAvailableGeminiModels(apiKey)
        result.fold(
            onSuccess = { models ->
                if (models.isEmpty()) {
                    _availableGeminiModelsState.value = UiState.Error("NO_MODELS_AVAILABLE")
                } else {
                    val savedId = themePreferences.customGeminiModelId.first()?.trim()
                    val matched = models.firstOrNull { it.modelId == savedId }
                    if (matched == null) {
                        val first = models.first()
                        themePreferences.setCustomGeminiModel(first.modelId, first.displayName)
                    } else if (themePreferences.customGeminiModelName.first() != matched.displayName) {
                        themePreferences.setCustomGeminiModel(matched.modelId, matched.displayName)
                    }
                    _availableGeminiModelsState.value = UiState.Success(models)
                }
            },
            onFailure = { err ->
                _availableGeminiModelsState.value = UiState.Error(err.localizedMessage ?: "MODEL_FETCH_ERROR")
            }
        )
    }

    fun saveProfile(
        displayName: String,
        handle: String,
        avatarOption: AvatarOption,
        bio: String,
        favoriteCategory: TriviaCategory,
        successMessage: String,
        onSaved: () -> Unit
    ) {
        val uid = currentUserId ?: return
        if (_isSaving.value) return
        viewModelScope.launch {
            _isSaving.value = true
            val res = progressRepository.updateCustomProfile(
                userId = uid,
                displayName = displayName,
                handle = handle,
                avatarId = avatarOption,
                bio = bio,
                favoriteCategory = favoriteCategory
            )
            _isSaving.value = false
            res.fold(
                onSuccess = {
                    _feedbackMessage.value = successMessage
                    onSaved()
                },
                onFailure = { err ->
                    _feedbackMessage.value = err.localizedMessage ?: "Unable to update profile"
                }
            )
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themePreferences.setThemeMode(mode)
        }
    }

    fun setAppLanguage(language: AppLanguage, onApplied: () -> Unit) {
        viewModelScope.launch {
            themePreferences.setAppLanguage(language)
            onApplied()
        }
    }

    fun setTimerEnabled(enabled: Boolean) {
        viewModelScope.launch {
            themePreferences.setTimerEnabled(enabled)
        }
    }

    fun selectPlayMode(mode: QuizMode) {
        viewModelScope.launch {
            themePreferences.setPlayMode(mode)
        }
    }

    fun selectQuestionCount(count: Int) {
        viewModelScope.launch {
            themePreferences.setQuestionCount(count)
        }
    }

    fun selectDifficulty(difficulty: QuizDifficulty) {
        viewModelScope.launch {
            themePreferences.setDifficulty(difficulty)
        }
    }

    fun setOfflineModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            themePreferences.setOfflineModeEnabled(enabled)
            if (enabled) {
                triviaRepository.ensureSeeded()
            }
            _feedbackMessage.value = if (enabled) "OFFLINE_MODE_ON" else "OFFLINE_MODE_OFF"
        }
    }

    fun syncOfflinePack() {
        refreshLibrary()
    }

    fun refreshLibrary() {
        if (_isSyncing.value) return
        viewModelScope.launch {
            _isSyncing.value = true
            val res = triviaRepository.refreshLibrary()
            _isSyncing.value = false
            res.fold(
                onSuccess = { total ->
                    _feedbackMessage.value = "REFRESHED:$total"
                },
                onFailure = { err ->
                    _feedbackMessage.value = err.localizedMessage ?: "SYNC_ERROR"
                }
            )
        }
    }

    fun deleteAllLibrary() {
        if (_isSyncing.value) return
        viewModelScope.launch {
            triviaRepository.deleteAllLibrary()
            _feedbackMessage.value = "LIBRARY_DELETED"
        }
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    fun setCustomGeminiApiKey(key: String?) {
        viewModelScope.launch {
            val cleaned = key?.trim()?.takeIf { it.isNotEmpty() }
            themePreferences.setCustomGeminiApiKey(cleaned)
            _feedbackMessage.value = if (cleaned != null) "CUSTOM_KEY_SAVED" else null
        }
    }

    fun refreshAvailableGeminiModels() {
        val currentKey = themePreferences.customGeminiApiKey.value?.trim().orEmpty()
        if (currentKey.isBlank()) {
            _availableGeminiModelsState.value = UiState.Success(emptyList())
            return
        }
        viewModelScope.launch {
            loadAvailableModels(currentKey)
        }
    }

    fun selectCustomGeminiModel(model: GeminiAvailableModel) {
        viewModelScope.launch {
            themePreferences.setCustomGeminiModel(model.modelId, model.displayName)
        }
    }

    private companion object {
        const val TAG = "ProfileVM"
    }
}
