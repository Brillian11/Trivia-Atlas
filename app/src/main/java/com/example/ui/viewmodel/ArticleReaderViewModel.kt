package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ThemePreferences
import com.example.data.model.AppLanguage
import com.example.data.model.QuizDifficulty
import com.example.data.model.QuizMode
import com.example.data.model.WikipediaArticle
import com.example.data.repository.WikipediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ArticleReaderViewModel(
    private val currentUserId: String?,
    private val pageId: Long,
    private val wikiLang: String,
    val fallbackTitle: String?,
    private val activeLangCode: String,
    private val wikipediaRepository: WikipediaRepository,
    private val themePreferences: ThemePreferences
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<WikipediaArticle>>(UiState.Loading)
    val state: StateFlow<UiState<WikipediaArticle>> = _state.asStateFlow()

    val customGeminiApiKey: StateFlow<String?> = themePreferences.customGeminiApiKey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    val customGeminiModelId: StateFlow<String?> = themePreferences.customGeminiModelId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    val customGeminiModelName: StateFlow<String?> = themePreferences.customGeminiModelName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    init {
        loadArticle()
    }

    fun loadArticle() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            val res = wikipediaRepository.getFullArticleContent(
                currentUserId = currentUserId,
                wikiLang = wikiLang,
                pageId = pageId,
                fallbackTitle = fallbackTitle
            )
            _state.value = res.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "ERROR") }
            )
        }
    }

    suspend fun buildPromptForArticle(
        article: WikipediaArticle,
        difficulty: QuizDifficulty,
        mode: QuizMode,
        count: Int
    ): String {
        val savedLang = themePreferences.appLanguage.first()
        val effectiveLang = if (savedLang == AppLanguage.SYSTEM) activeLangCode else savedLang.code
        return wikipediaRepository.buildUserAiQuizPrompt(
            article = article,
            languageCode = effectiveLang,
            difficulty = difficulty,
            mode = mode,
            requestedCount = count
        )
    }

    fun saveKeyAndStartGeminiQuiz(
        rawKey: String,
        onReady: () -> Unit
    ) {
        viewModelScope.launch {
            val cleaned = rawKey.trim()
            if (cleaned.isNotEmpty()) {
                themePreferences.setCustomGeminiApiKey(cleaned)
            }
            onReady()
        }
    }

    fun importAiQuizJson(
        article: WikipediaArticle,
        rawResponse: String,
        onSuccess: () -> Unit,
        onInvalid: () -> Unit
    ) {
        val result = wikipediaRepository.importCustomAiQuizJson(
            article = article,
            rawResponse = rawResponse
        )
        if (result.isSuccess) {
            onSuccess()
        } else {
            onInvalid()
        }
    }
}
