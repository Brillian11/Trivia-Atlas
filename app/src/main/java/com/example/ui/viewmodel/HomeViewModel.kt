package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.UserProgressRepository
import com.example.data.local.ThemePreferences
import com.example.data.model.AppLanguage
import com.example.data.model.GuestStatsSummary
import com.example.data.model.ProgressionSystem
import com.example.data.model.QuizDifficulty
import com.example.data.model.QuizMode
import com.example.data.model.UserProfile
import com.example.data.model.WikipediaArticle
import com.example.data.remote.NetworkMonitor
import com.example.data.repository.TriviaRepository
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class HomeViewModel(
    private val currentUserId: String?,
    private val googleDisplayName: String?,
    private val activeLangCode: String,
    private val triviaRepository: TriviaRepository,
    private val wikipediaRepository: WikipediaRepository,
    private val progressRepository: UserProgressRepository,
    private val networkMonitor: NetworkMonitor,
    private val themePreferences: ThemePreferences
) : ViewModel() {

    val isGuest: Boolean = currentUserId == null

    val totalCachedCount: StateFlow<Int> = triviaRepository.totalCachedCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), 0)

    val pictureQuestionCount: StateFlow<Int> = triviaRepository.pictureQuestionCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), 0)

    val categoryCounts: StateFlow<Map<String, Int>> = triviaRepository.categoryCacheCounts
        .map { list -> list.associate { it.category to it.count } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyMap())

    val guestStats: StateFlow<GuestStatsSummary> = triviaRepository.observeGuestStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), GuestStatsSummary())

    val profileState: StateFlow<UiState<UserProfile?>> = if (currentUserId != null) {
        progressRepository.observeUserProfile(currentUserId)
            .map<UserProfile?, UiState<UserProfile?>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing profile", rootError)
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

    private val userObjectiveKeyFlow = if (currentUserId != null) {
        flowOf(currentUserId)
    } else {
        themePreferences.guestDeviceIdFlow
    }

    val dailyObjectives: StateFlow<List<com.example.data.model.DailyObjectiveProgressItem>> = combine(
        streakSnapshot,
        userObjectiveKeyFlow
    ) { snapshot, userKey ->
        com.example.data.model.DailyObjectives.progressItemsForDay(
            snapshot = snapshot,
            todayDayKey = ProgressionSystem.localDayKey(),
            userKey = userKey
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        com.example.data.model.DailyObjectives.progressItemsForDay(
            snapshot = ProgressionSystem.StreakSnapshot(),
            todayDayKey = ProgressionSystem.localDayKey(),
            userKey = currentUserId.orEmpty()
        )
    )

    val selectedDifficulty: StateFlow<QuizDifficulty> = themePreferences.difficulty
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), QuizDifficulty.ALL)

    val selectedPlayMode: StateFlow<QuizMode> = themePreferences.playMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), QuizMode.STANDARD)

    val selectedQuestionCount: StateFlow<Int> = themePreferences.questionCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), 10)

    private val _wikiCarouselState = MutableStateFlow<UiState<List<WikipediaArticle>>>(UiState.Loading)
    val wikiCarouselState: StateFlow<UiState<List<WikipediaArticle>>> = _wikiCarouselState.asStateFlow()

    private val _wikiSearchQuery = MutableStateFlow("")
    val wikiSearchQuery: StateFlow<String> = _wikiSearchQuery.asStateFlow()

    private val _wikiSearchState = MutableStateFlow<UiState<List<WikipediaArticle>>?>(null)
    val wikiSearchState: StateFlow<UiState<List<WikipediaArticle>>?> = _wikiSearchState.asStateFlow()

    private val _customUrlLoading = MutableStateFlow(false)
    val customUrlLoading: StateFlow<Boolean> = _customUrlLoading.asStateFlow()

    private val _customUrlError = MutableStateFlow<String?>(null)
    val customUrlError: StateFlow<String?> = _customUrlError.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private val checkInMutex = Mutex()
    private var lastCheckedDayKey: String? = null
    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            if (themePreferences.offlineModeEnabled.first()) {
                triviaRepository.ensureSeeded()
            }
            if (currentUserId != null) {
                progressRepository.ensureProfileInitialized(currentUserId, googleDisplayName)
            } else {
                themePreferences.getOrCreateGuestDeviceId()
            }
            onAppOpened()
        }
        viewModelScope.launch {
            themePreferences.appLanguage.collectLatest { savedLang ->
                val effective = if (savedLang == AppLanguage.SYSTEM) activeLangCode else savedLang.code
                loadDailyWikipediaCarousel(languageCode = effective, forceRefresh = false)
            }
        }
        viewModelScope.launch {
            networkMonitor.isOnline.collectLatest { online ->
                if (online) {
                    if (_wikiCarouselState.value is UiState.Error) {
                        val savedLang = themePreferences.appLanguage.first()
                        val effective = if (savedLang == AppLanguage.SYSTEM) activeLangCode else savedLang.code
                        loadDailyWikipediaCarousel(languageCode = effective, forceRefresh = false)
                    }
                    if (currentUserId != null) {
                        val synced = triviaRepository.flushPendingScores(
                            userId = currentUserId,
                            progressRepo = progressRepository,
                            fallbackDisplayName = googleDisplayName
                        )
                        if (synced > 0) {
                            _statusBanner.value = "Synced $synced offline round(s)"
                        }
                    }
                }
            }
        }
    }

    fun onAppOpened() {
        viewModelScope.launch {
            checkInMutex.withLock {
                val today = ProgressionSystem.localDayKey()
                if (lastCheckedDayKey == today) return@withLock

                val outcome = if (currentUserId != null) {
                    val res = progressRepository.recordDailyCheckIn(currentUserId, googleDisplayName)
                    if (res.isFailure) return@withLock
                    res.getOrNull()
                } else {
                    themePreferences.recordGuestDailyCheckIn()
                }

                if (outcome != null) {
                    lastCheckedDayKey = today
                    when {
                        outcome.freezesConsumed > 0 -> {
                            _statusBanner.value =
                                "STREAK_FROZEN_SAVED:${outcome.updated.dailyStreak}:${outcome.updated.streakFreezes}"
                        }
                        outcome.checkedInToday && outcome.updated.dailyStreak > 1 -> {
                            _statusBanner.value = "STREAK_EXTENDED:${outcome.updated.dailyStreak}"
                        }
                    }
                }
            }
        }
    }

    fun ensureDailyRefresh() {
        val currentArticles = (_wikiCarouselState.value as? UiState.Success)?.data
        val today = wikipediaRepository.currentDateKey()
        if (currentArticles == null || currentArticles.firstOrNull()?.dateKey != today) {
            refreshWikipediaCarousel(forceRefresh = false)
        }
    }

    fun refreshWikipediaCarousel(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val savedLang = themePreferences.appLanguage.first()
            val effective = if (savedLang == AppLanguage.SYSTEM) activeLangCode else savedLang.code
            loadDailyWikipediaCarousel(languageCode = effective, forceRefresh = forceRefresh)
        }
    }

    private suspend fun loadDailyWikipediaCarousel(languageCode: String, forceRefresh: Boolean) {
        _wikiCarouselState.value = UiState.Loading
        val result = wikipediaRepository.getDailyCarouselArticles(
            currentUserId = currentUserId,
            languageCode = languageCode,
            forceRefresh = forceRefresh
        )
        _wikiCarouselState.value = result.fold(
            onSuccess = { articles -> UiState.Success(articles) },
            onFailure = { error -> UiState.Error(error.message ?: "ERROR") }
        )
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

    fun openSearchedArticle(article: WikipediaArticle) {
        wikipediaRepository.cacheArticleInMemory(article)
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

    fun clearCustomUrlError() {
        _customUrlError.value = null
    }

    fun openCustomArticleUrl(
        rawUrl: String,
        onLoaded: (WikipediaArticle) -> Unit
    ) {
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
                    onLoaded(article)
                },
                onFailure = { err ->
                    _customUrlError.value = err.message ?: "ARTICLE_NOT_READABLE"
                }
            )
        }
    }

    fun dismissBanner() {
        _statusBanner.value = null
    }

    private companion object {
        const val TAG = "HomeViewModel"
    }
}
