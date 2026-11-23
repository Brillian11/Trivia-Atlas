package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.BattleRepository
import com.example.data.firebase.UserProgressRepository
import com.example.data.local.ThemePreferences
import com.example.data.model.AppLanguage
import com.example.data.model.BattleInvite
import com.example.data.model.BattleRoom
import com.example.data.model.LeaderboardEntry
import com.example.data.model.QuizDifficulty
import com.example.data.model.QuizMode
import com.example.data.model.TriviaCategory
import com.example.data.model.WikipediaArticle
import com.example.data.repository.WikipediaRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BattleHubViewModel(
    private val currentUserId: String?,
    private val googleDisplayName: String?,
    private val activeLangCode: String,
    private val battleRepository: BattleRepository,
    private val progressRepository: UserProgressRepository,
    private val wikipediaRepository: WikipediaRepository,
    private val themePreferences: ThemePreferences
) : ViewModel() {

    val isGuest: Boolean = currentUserId == null

    val incomingInvites: StateFlow<List<BattleInvite>> = if (currentUserId != null) {
        battleRepository.observeIncomingInvites(currentUserId)
            .catch { error ->
                if (error is CancellationException) throw error
                Log.w(TAG, "Error observing incoming battle invites", error)
                emit(emptyList())
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())
    } else {
        flowOf(emptyList<BattleInvite>())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())
    }

    val myRooms: StateFlow<UiState<List<BattleRoom>>> = if (currentUserId != null) {
        battleRepository.observeMyRooms(currentUserId)
            .map<List<BattleRoom>, UiState<List<BattleRoom>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing my battle rooms", rootError)
                emit(UiState.Error(rootError.localizedMessage ?: "Failed to load battle rooms"))
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)
    } else {
        flowOf<UiState<List<BattleRoom>>>(UiState.Success(emptyList()))
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Success(emptyList()))
    }

    val explorers: StateFlow<List<LeaderboardEntry>> = if (currentUserId != null) {
        battleRepository.observeExplorersForInvite(currentUserId)
            .catch { error ->
                if (error is CancellationException) throw error
                Log.w(TAG, "Error observing explorers for invite", error)
                emit(emptyList())
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())
    } else {
        flowOf(emptyList<LeaderboardEntry>())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())
    }

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _busyInviteId = MutableStateFlow<String?>(null)
    val busyInviteId: StateFlow<String?> = _busyInviteId.asStateFlow()

    private val _errorCode = MutableStateFlow<String?>(null)
    val errorCode: StateFlow<String?> = _errorCode.asStateFlow()

    private val _wikiSearchQuery = MutableStateFlow("")
    val wikiSearchQuery: StateFlow<String> = _wikiSearchQuery.asStateFlow()

    private val _wikiSearchState = MutableStateFlow<UiState<List<WikipediaArticle>>?>(null)
    val wikiSearchState: StateFlow<UiState<List<WikipediaArticle>>?> = _wikiSearchState.asStateFlow()

    private val _selectedBattleArticle = MutableStateFlow<WikipediaArticle?>(null)
    val selectedBattleArticle: StateFlow<WikipediaArticle?> = _selectedBattleArticle.asStateFlow()

    private val _customUrlLoading = MutableStateFlow(false)
    val customUrlLoading: StateFlow<Boolean> = _customUrlLoading.asStateFlow()

    private val _customUrlError = MutableStateFlow<String?>(null)
    val customUrlError: StateFlow<String?> = _customUrlError.asStateFlow()

    private var searchJob: Job? = null

    init {
        if (currentUserId != null) {
            viewModelScope.launch {
                runCatching {
                    progressRepository.ensureProfileInitialized(currentUserId, googleDisplayName)
                }
            }
        }
    }

    fun clearError() {
        _errorCode.value = null
    }

    fun onWikiSearchQueryChanged(query: String) {
        _wikiSearchQuery.value = query
        searchJob?.cancel()
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            _wikiSearchState.value = null
            return
        }
        if (trimmed.length < 2) {
            _wikiSearchState.value = UiState.Success(emptyList())
            return
        }
        searchJob = viewModelScope.launch {
            delay(400L)
            executeWikipediaSearch(trimmed)
        }
    }

    fun submitWikipediaSearch() {
        searchJob?.cancel()
        val trimmed = _wikiSearchQuery.value.trim()
        if (trimmed.length < 2) return
        searchJob = viewModelScope.launch {
            executeWikipediaSearch(trimmed)
        }
    }

    fun clearWikiSearch() {
        searchJob?.cancel()
        _wikiSearchQuery.value = ""
        _wikiSearchState.value = null
    }

    private suspend fun executeWikipediaSearch(query: String) {
        _wikiSearchState.value = UiState.Loading
        val savedLang = themePreferences.appLanguage.first()
        val effective = if (savedLang == AppLanguage.SYSTEM) activeLangCode else savedLang.code
        val result = wikipediaRepository.searchWikipediaArticles(
            query = query,
            languageCode = effective
        )
        _wikiSearchState.value = result.fold(
            onSuccess = { list -> UiState.Success(list) },
            onFailure = { err -> UiState.Error(err.message ?: "ERROR") }
        )
    }

    fun selectBattleArticle(article: WikipediaArticle) {
        wikipediaRepository.cacheArticleInMemory(article)
        _selectedBattleArticle.value = article
        _customUrlError.value = null
    }

    fun clearSelectedBattleArticle() {
        _selectedBattleArticle.value = null
    }

    fun clearCustomUrlError() {
        _customUrlError.value = null
    }

    fun loadCustomBattleArticleUrl(rawUrl: String) {
        if (_customUrlLoading.value) return
        viewModelScope.launch {
            _customUrlLoading.value = true
            _customUrlError.value = null
            val savedLang = themePreferences.appLanguage.first()
            val effective = if (savedLang == AppLanguage.SYSTEM) activeLangCode else savedLang.code
            val res = wikipediaRepository.loadCustomArticleFromUrl(
                rawUrl = rawUrl,
                languageCode = effective
            )
            _customUrlLoading.value = false
            res.fold(
                onSuccess = { article ->
                    _selectedBattleArticle.value = article
                },
                onFailure = { err ->
                    _customUrlError.value = err.message ?: "ARTICLE_NOT_READABLE"
                }
            )
        }
    }

    fun resetArticleSelection() {
        searchJob?.cancel()
        _wikiSearchQuery.value = ""
        _wikiSearchState.value = null
        _selectedBattleArticle.value = null
        _customUrlError.value = null
        _customUrlLoading.value = false
    }

    fun joinRoomByCode(rawCode: String, onJoined: (String) -> Unit) {
        if (currentUserId == null || _isSubmitting.value) return
        val normalized = battleRepository.normalizeCode(rawCode)
        if (normalized.length !in 4..12) {
            _errorCode.value = "ROOM_CODE_INVALID"
            return
        }
        viewModelScope.launch {
            _isSubmitting.value = true
            _errorCode.value = null
            val res = battleRepository.joinRoomByCode(normalized, googleDisplayName)
            _isSubmitting.value = false
            res.fold(
                onSuccess = { code -> onJoined(code) },
                onFailure = { err -> _errorCode.value = mapErrorMessage(err) }
            )
        }
    }

    fun respondToInvite(
        invite: BattleInvite,
        accept: Boolean,
        onJoined: (String) -> Unit
    ) {
        if (currentUserId == null || _busyInviteId.value != null) return
        viewModelScope.launch {
            _busyInviteId.value = invite.id
            _errorCode.value = null
            val res = battleRepository.respondToInvite(invite, accept, googleDisplayName)
            _busyInviteId.value = null
            res.fold(
                onSuccess = { joinedCode ->
                    if (accept && !joinedCode.isNullOrBlank()) {
                        onJoined(joinedCode)
                    }
                },
                onFailure = { err -> _errorCode.value = mapErrorMessage(err) }
            )
        }
    }

    fun createRoom(
        topics: Set<TriviaCategory>,
        difficulty: QuizDifficulty,
        mode: QuizMode,
        questionCount: Int,
        selectedInvitees: List<LeaderboardEntry>,
        selectedArticle: WikipediaArticle? = null,
        customUrlToLoad: String? = null,
        onCreated: (String) -> Unit
    ) {
        if (currentUserId == null || _isSubmitting.value) return
        viewModelScope.launch {
            _isSubmitting.value = true
            _errorCode.value = null
            _customUrlError.value = null

            val savedLang = themePreferences.appLanguage.first()
            val effectiveLanguage = if (savedLang == AppLanguage.SYSTEM) {
                activeLangCode
            } else {
                savedLang.code
            }

            var resolvedArticle = if (mode == QuizMode.PICTURE) null else selectedArticle
            if (mode != QuizMode.PICTURE && resolvedArticle == null && !customUrlToLoad.isNullOrBlank()) {
                val loaded = wikipediaRepository.loadCustomArticleFromUrl(
                    rawUrl = customUrlToLoad,
                    languageCode = effectiveLanguage
                )
                if (loaded.isFailure) {
                    _isSubmitting.value = false
                    _customUrlError.value = loaded.exceptionOrNull()?.message ?: "ARTICLE_NOT_READABLE"
                    return@launch
                }
                resolvedArticle = loaded.getOrNull()
                _selectedBattleArticle.value = resolvedArticle
            }

            val resolvedTopics = when {
                mode == QuizMode.PICTURE -> listOf(TriviaCategory.PICTURE_ATLAS)
                resolvedArticle != null -> listOf(TriviaCategory.WIKIPEDIA)
                else -> topics
                    .filter { it != TriviaCategory.WIKIPEDIA && it != TriviaCategory.PICTURE_ATLAS }
                    .ifEmpty { listOf(TriviaCategory.GENERAL_KNOWLEDGE) }
            }

            val res = battleRepository.createRoom(
                googleDisplayName = googleDisplayName,
                topics = resolvedTopics,
                difficulty = difficulty,
                mode = mode,
                questionCount = questionCount,
                languageCode = effectiveLanguage,
                initialInvitees = selectedInvitees,
                customArticle = resolvedArticle
            )
            _isSubmitting.value = false
            res.fold(
                onSuccess = { code ->
                    resetArticleSelection()
                    onCreated(code)
                },
                onFailure = { err -> _errorCode.value = mapErrorMessage(err) }
            )
        }
    }

    private fun mapErrorMessage(err: Throwable): String {
        val msg = err.message.orEmpty()
        return when {
            msg.contains("ROOM_CODE_INVALID") -> "ROOM_CODE_INVALID"
            msg.contains("ROOM_NOT_FOUND") -> "ROOM_NOT_FOUND"
            msg.contains("ROOM_ALREADY_STARTED") -> "ROOM_ALREADY_STARTED"
            msg.contains("ROOM_FULL") -> "ROOM_FULL"
            msg.contains("ARTICLE_QUIZ_FAILED") -> "ARTICLE_QUIZ_FAILED"
            else -> err.localizedMessage ?: "BATTLE_ACTION_FAILED"
        }
    }

    private companion object {
        const val TAG = "BattleHubVM"
    }
}
