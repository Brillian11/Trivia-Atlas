package com.example.ui.viewmodel

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.firebase.UserProgressRepository
import com.example.data.model.ProgressionSystem
import com.example.data.model.WeeklyRankingCycle
import com.example.data.model.WeeklyRankingEntry
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive

enum class LeaderboardSort(@StringRes val labelRes: Int) {
    TOTAL_SCORE(R.string.sort_weekly_xp),
    HIGH_SCORE(R.string.sort_high_score),
    BEST_STREAK(R.string.sort_best_streak)
}

@OptIn(ExperimentalCoroutinesApi::class)
class LeaderboardViewModel(
    currentUserId: String?,
    progressRepository: UserProgressRepository
) : ViewModel() {

    val isGuest: Boolean = currentUserId == null

    private val _sortOption = MutableStateFlow(LeaderboardSort.TOTAL_SCORE)
    val sortOption: StateFlow<LeaderboardSort> = _sortOption.asStateFlow()

    private val clockFlow: Flow<Long> = flow {
        while (currentCoroutineContext().isActive) {
            emit(System.currentTimeMillis())
            delay(1000L)
        }
    }

    val weeklyCycle: StateFlow<WeeklyRankingCycle> = clockFlow
        .map { now -> ProgressionSystem.currentWeeklyCycle(now) }
        .distinctUntilChanged { old, new -> old.cycleId == new.cycleId }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ProgressionSystem.currentWeeklyCycle()
        )

    val remainingUntilResetMillis: StateFlow<Long> = combine(
        clockFlow,
        weeklyCycle
    ) { now, cycle -> cycle.remainingMillis(now) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ProgressionSystem.currentWeeklyCycle().remainingMillis()
        )

    val leaderboardState: StateFlow<UiState<List<WeeklyRankingEntry>>> = if (currentUserId != null) {
        combine(
            weeklyCycle
                .map { it.cycleId }
                .distinctUntilChanged()
                .flatMapLatest { cycleId -> progressRepository.observeWeeklyLeaderboard(cycleId) },
            _sortOption
        ) { entries, sort ->
            val sorted = when (sort) {
                LeaderboardSort.TOTAL_SCORE -> entries.sortedWith(
                    compareByDescending<WeeklyRankingEntry> { it.weeklyXp }
                        .thenByDescending { it.highScore }
                        .thenByDescending { it.level }
                )
                LeaderboardSort.HIGH_SCORE -> entries.sortedWith(
                    compareByDescending<WeeklyRankingEntry> { it.highScore }
                        .thenByDescending { it.weeklyXp }
                )
                LeaderboardSort.BEST_STREAK -> entries.sortedWith(
                    compareByDescending<WeeklyRankingEntry> { it.bestStreak }
                        .thenByDescending { it.weeklyXp }
                )
            }
            UiState.Success(sorted) as UiState<List<WeeklyRankingEntry>>
        }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing weekly leaderboard", rootError)
                emit(UiState.Error(rootError.localizedMessage ?: "Failed to load global leaderboard"))
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Loading)
    } else {
        flowOf<UiState<List<WeeklyRankingEntry>>>(UiState.Success(emptyList()))
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiState.Success(emptyList()))
    }

    fun setSortOption(option: LeaderboardSort) {
        _sortOption.value = option
    }

    private companion object {
        const val TAG = "LeaderboardVM"
    }
}
