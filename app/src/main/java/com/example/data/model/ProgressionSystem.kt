package com.example.data.model

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class LevelProgress(
    val level: Int,
    val lifetimeXp: Long,
    val xpIntoCurrentLevel: Long,
    val xpRequiredForCurrentLevel: Long,
    val cumulativeThresholdForCurrentLevel: Long,
    val cumulativeThresholdForNextLevel: Long
) {
    val progressFraction: Float
        get() = if (xpRequiredForCurrentLevel <= 0L) {
            0f
        } else {
            (xpIntoCurrentLevel.toDouble() / xpRequiredForCurrentLevel.toDouble())
                .toFloat()
                .coerceIn(0f, 1f)
        }

    val xpRemainingToNextLevel: Long
        get() = (xpRequiredForCurrentLevel - xpIntoCurrentLevel).coerceAtLeast(0L)
}

data class WeeklyRankingCycle(
    val cycleId: String,
    val cycleStartMillis: Long,
    val nextResetMillis: Long
) {
    fun remainingMillis(nowMillis: Long = System.currentTimeMillis()): Long =
        (nextResetMillis - nowMillis).coerceAtLeast(0L)
}

object ProgressionSystem {
    const val BASE_LEVEL_XP = 10_000L
    const val DAY_MILLIS = 24L * 60L * 60L * 1000L
    const val WEEK_MILLIS = 7L * DAY_MILLIS
    private const val MAX_SHIFT = 40

    fun isWithinResetCountdownWindow(remainingMillis: Long): Boolean =
        remainingMillis in 1L..DAY_MILLIS

    private val gmtTimeZone: TimeZone
        get() = TimeZone.getTimeZone("GMT")

    /**
     * XP required solely to complete [level] and advance to [level] + 1.
     * Level 1 = 10,000 XP, Level 2 = 20,000 XP, Level 3 = 40,000 XP, doubling each level.
     */
    fun xpRequiredForLevel(level: Int): Long {
        val safeLevel = level.coerceAtLeast(1)
        val shift = (safeLevel - 1).coerceIn(0, MAX_SHIFT)
        return BASE_LEVEL_XP shl shift
    }

    /**
     * Computes lifetime level progress from cumulative [lifetimeXp].
     * Lifetime XP never resets.
     */
    fun calculateLevelProgress(lifetimeXp: Long): LevelProgress {
        val safeXp = lifetimeXp.coerceAtLeast(0L)
        var level = 1
        var remaining = safeXp
        var cumulativeStart = 0L

        while (level < MAX_SHIFT + 1) {
            val req = xpRequiredForLevel(level)
            if (remaining < req) {
                return LevelProgress(
                    level = level,
                    lifetimeXp = safeXp,
                    xpIntoCurrentLevel = remaining,
                    xpRequiredForCurrentLevel = req,
                    cumulativeThresholdForCurrentLevel = cumulativeStart,
                    cumulativeThresholdForNextLevel = cumulativeStart + req
                )
            }
            remaining -= req
            cumulativeStart += req
            level++
        }

        val capReq = xpRequiredForLevel(level)
        return LevelProgress(
            level = level,
            lifetimeXp = safeXp,
            xpIntoCurrentLevel = remaining,
            xpRequiredForCurrentLevel = capReq,
            cumulativeThresholdForCurrentLevel = cumulativeStart,
            cumulativeThresholdForNextLevel = cumulativeStart + capReq
        )
    }

    /**
     * Returns the weekly ranking cycle anchored to Sunday 12:00:00.000 GMT.
     * Whenever the clock reaches Sunday 12:00 PM GMT, [cycleId] advances immediately,
     * expiring the prior weekend's ranking and resetting ranking XP to 0 for the new week.
     */
    fun currentWeeklyCycle(nowMillis: Long = System.currentTimeMillis()): WeeklyRankingCycle {
        val cal = Calendar.getInstance(gmtTimeZone, Locale.US).apply {
            firstDayOfWeek = Calendar.SUNDAY
            timeInMillis = nowMillis
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis > nowMillis) {
                add(Calendar.DAY_OF_YEAR, -7)
            }
        }
        val startMillis = cal.timeInMillis
        val nextResetMillis = startMillis + WEEK_MILLIS
        val formatter = SimpleDateFormat("yyyy-MM-dd'_1200Z'", Locale.US).apply {
            timeZone = gmtTimeZone
        }
        return WeeklyRankingCycle(
            cycleId = formatter.format(Date(startMillis)),
            cycleStartMillis = startMillis,
            nextResetMillis = nextResetMillis
        )
    }

    fun formatXp(amount: Long, locale: Locale = Locale.getDefault()): String {
        return NumberFormat.getIntegerInstance(locale).format(amount.coerceAtLeast(0L))
    }

    const val MAX_STREAK_FREEZES = 3
    const val DAILY_XP_FREEZE_THRESHOLD = 10_000L
    const val OBJECTIVE_STREAK_TARGET_DAYS = 3

    data class StreakSnapshot(
        val dailyStreak: Int = 0,
        val bestDailyStreak: Int = 0,
        val streakFreezes: Int = 0,
        val lastActiveDay: String = "",
        val lastFreezeUsedDay: String = "",
        val dailyXpEarned: Long = 0L,
        val dailyXpDate: String = "",
        val dailyXpFreezeGrantedDate: String = "",
        val objectiveDayKey: String = "",
        val completedObjectiveIds: List<String> = emptyList(),
        val dailyRoundsCompleted: Int = 0,
        val dailyObjectiveStreak: Int = 0,
        val lastObjectiveCompletedDay: String = "",
        val objectiveFreezeGrantedDate: String = ""
    ) {
        fun dailyXpFor(todayDayKey: String): Long =
            if (dailyXpDate == todayDayKey) dailyXpEarned.coerceAtLeast(0L) else 0L

        fun hasEarnedDailyXpFreezeOn(todayDayKey: String): Boolean =
            dailyXpDate == todayDayKey && dailyXpFreezeGrantedDate == todayDayKey

        fun completedObjectivesFor(todayDayKey: String): List<String> =
            if (objectiveDayKey == todayDayKey) completedObjectiveIds.distinct().take(3) else emptyList()

        fun roundsCompletedOn(todayDayKey: String): Int =
            if (objectiveDayKey == todayDayKey) dailyRoundsCompleted.coerceAtLeast(0) else 0

        /**
         * Returns the active objective streak progress out of [OBJECTIVE_STREAK_TARGET_DAYS] (0..3) for UI display.
         */
        fun effectiveObjectiveStreak(todayDayKey: String): Int {
            if (lastObjectiveCompletedDay.isBlank() || dailyObjectiveStreak <= 0) return 0
            if (lastObjectiveCompletedDay == todayDayKey) {
                return dailyObjectiveStreak.coerceIn(0, OBJECTIVE_STREAK_TARGET_DAYS)
            }
            val gap = daysBetweenDayKeys(lastObjectiveCompletedDay, todayDayKey)
            return if (gap == 1 && objectiveFreezeGrantedDate != lastObjectiveCompletedDay) {
                dailyObjectiveStreak.coerceIn(0, OBJECTIVE_STREAK_TARGET_DAYS)
            } else {
                0
            }
        }
    }

    data class DailyCheckInOutcome(
        val updated: StreakSnapshot,
        val checkedInToday: Boolean,
        val freezesConsumed: Int = 0,
        val streakLost: Boolean = false
    )

    data class FreezeRewardOutcome(
        val updated: StreakSnapshot,
        val freezesEarnedFromLevelUp: Int = 0,
        val freezeEarnedFromDailyXp: Boolean = false,
        val freezeEarnedFromObjectives: Boolean = false,
        val newlyCompletedObjectiveIds: List<String> = emptyList()
    ) {
        val totalFreezesEarned: Int
            get() = freezesEarnedFromLevelUp +
                (if (freezeEarnedFromDailyXp) 1 else 0) +
                (if (freezeEarnedFromObjectives) 1 else 0)
    }

    fun localDayKey(
        nowMillis: Long = System.currentTimeMillis(),
        timeZone: TimeZone = TimeZone.getDefault()
    ): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            this.timeZone = timeZone
        }
        return formatter.format(Date(nowMillis))
    }

    fun daysBetweenDayKeys(earlierDayKey: String, laterDayKey: String): Int {
        if (earlierDayKey.isBlank() || laterDayKey.isBlank()) return -1
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
                timeZone = gmtTimeZone
                isLenient = false
            }
            val earlier = parser.parse(earlierDayKey.trim()) ?: return -1
            val later = parser.parse(laterDayKey.trim()) ?: return -1
            ((later.time - earlier.time) / DAY_MILLIS).toInt()
        } catch (_: Exception) {
            -1
        }
    }

    /**
     * Evaluates a daily app-open check-in for [todayDayKey].
     * - First visit: starts streak at 1.
     * - Same day: preserves streak and rolls over daily counters only if date changed.
     * - Next consecutive calendar day: increments streak by 1.
     * - Missed 1..3 days covered by available Streak Freezes: consumes missed-day freezes,
     *   saves the streak, and advances streak by 1 for opening today.
     * - Missed more days than available freezes: streak resets to 1 and freezes clear to 0.
     */
    fun applyDailyCheckIn(
        current: StreakSnapshot,
        todayDayKey: String = localDayKey()
    ): DailyCheckInOutcome {
        val safeFreezes = current.streakFreezes.coerceIn(0, MAX_STREAK_FREEZES)
        val rolledDailyXp = if (current.dailyXpDate == todayDayKey) current.dailyXpEarned.coerceAtLeast(0L) else 0L
        val rolledGrantedDate = if (current.dailyXpDate == todayDayKey && current.dailyXpFreezeGrantedDate == todayDayKey) {
            todayDayKey
        } else {
            ""
        }
        val rolledObjectives = current.completedObjectivesFor(todayDayKey)
        val rolledRounds = current.roundsCompletedOn(todayDayKey)
        val rolledObjStreak = current.effectiveObjectiveStreak(todayDayKey)

        if (current.lastActiveDay.isBlank() || current.dailyStreak <= 0) {
            val updated = current.copy(
                dailyStreak = 1,
                bestDailyStreak = maxOf(current.bestDailyStreak, 1),
                streakFreezes = safeFreezes,
                lastActiveDay = todayDayKey,
                dailyXpEarned = rolledDailyXp,
                dailyXpDate = todayDayKey,
                dailyXpFreezeGrantedDate = rolledGrantedDate,
                objectiveDayKey = todayDayKey,
                completedObjectiveIds = rolledObjectives,
                dailyRoundsCompleted = rolledRounds,
                dailyObjectiveStreak = rolledObjStreak
            )
            return DailyCheckInOutcome(updated = updated, checkedInToday = true)
        }

        val gapDays = daysBetweenDayKeys(current.lastActiveDay, todayDayKey)
        if (gapDays <= 0) {
            val updated = current.copy(
                dailyStreak = current.dailyStreak.coerceAtLeast(1),
                bestDailyStreak = maxOf(current.bestDailyStreak, current.dailyStreak.coerceAtLeast(1)),
                streakFreezes = safeFreezes,
                dailyXpEarned = rolledDailyXp,
                dailyXpDate = todayDayKey,
                dailyXpFreezeGrantedDate = rolledGrantedDate,
                objectiveDayKey = todayDayKey,
                completedObjectiveIds = rolledObjectives,
                dailyRoundsCompleted = rolledRounds,
                dailyObjectiveStreak = rolledObjStreak
            )
            return DailyCheckInOutcome(updated = updated, checkedInToday = false)
        }

        if (gapDays == 1) {
            val nextStreak = current.dailyStreak + 1
            val updated = current.copy(
                dailyStreak = nextStreak,
                bestDailyStreak = maxOf(current.bestDailyStreak, nextStreak),
                streakFreezes = safeFreezes,
                lastActiveDay = todayDayKey,
                dailyXpEarned = rolledDailyXp,
                dailyXpDate = todayDayKey,
                dailyXpFreezeGrantedDate = rolledGrantedDate,
                objectiveDayKey = todayDayKey,
                completedObjectiveIds = rolledObjectives,
                dailyRoundsCompleted = rolledRounds,
                dailyObjectiveStreak = rolledObjStreak
            )
            return DailyCheckInOutcome(updated = updated, checkedInToday = true)
        }

        val missedDays = gapDays - 1
        return if (safeFreezes >= missedDays) {
            val remainingFreezes = (safeFreezes - missedDays).coerceIn(0, MAX_STREAK_FREEZES)
            val nextStreak = current.dailyStreak + 1
            val updated = current.copy(
                dailyStreak = nextStreak,
                bestDailyStreak = maxOf(current.bestDailyStreak, nextStreak),
                streakFreezes = remainingFreezes,
                lastActiveDay = todayDayKey,
                lastFreezeUsedDay = todayDayKey,
                dailyXpEarned = rolledDailyXp,
                dailyXpDate = todayDayKey,
                dailyXpFreezeGrantedDate = rolledGrantedDate,
                objectiveDayKey = todayDayKey,
                completedObjectiveIds = rolledObjectives,
                dailyRoundsCompleted = rolledRounds,
                dailyObjectiveStreak = rolledObjStreak
            )
            DailyCheckInOutcome(
                updated = updated,
                checkedInToday = true,
                freezesConsumed = missedDays
            )
        } else {
            val updated = current.copy(
                dailyStreak = 1,
                bestDailyStreak = maxOf(current.bestDailyStreak, current.dailyStreak, 1),
                streakFreezes = 0,
                lastActiveDay = todayDayKey,
                dailyXpEarned = rolledDailyXp,
                dailyXpDate = todayDayKey,
                dailyXpFreezeGrantedDate = rolledGrantedDate,
                objectiveDayKey = todayDayKey,
                completedObjectiveIds = rolledObjectives,
                dailyRoundsCompleted = rolledRounds,
                dailyObjectiveStreak = rolledObjStreak
            )
            DailyCheckInOutcome(
                updated = updated,
                checkedInToday = true,
                streakLost = current.dailyStreak > 0
            )
        }
    }

    /**
     * Awards up to [MAX_STREAK_FREEZES] (3) Streak Freezes when the player:
     * 1) Earns at least [DAILY_XP_FREEZE_THRESHOLD] (10,000 XP) in a single day, OR
     * 2) Completes all 3 Daily Objectives for 3 consecutive days, OR
     * 3) Achieves a new Lifetime Explorer Level.
     */
    fun applyXpForStreakFreezes(
        current: StreakSnapshot,
        earnedXp: Long,
        prevLifetimeXp: Long,
        todayDayKey: String = localDayKey(),
        completedRound: QuizRoundResult? = null,
        hintsUsed: Int = 0,
        userKey: String = ""
    ): FreezeRewardOutcome {
        val safeEarned = earnedXp.coerceAtLeast(0L)
        val afterCheckIn = applyDailyCheckIn(current, todayDayKey).updated

        val levelBefore = calculateLevelProgress(prevLifetimeXp.coerceAtLeast(0L)).level
        val levelAfter = calculateLevelProgress(prevLifetimeXp.coerceAtLeast(0L) + safeEarned).level
        val levelUps = (levelAfter - levelBefore).coerceAtLeast(0)

        val baseDailyXp = afterCheckIn.dailyXpFor(todayDayKey)
        val newDailyXp = baseDailyXp + safeEarned
        val alreadyGrantedDaily = afterCheckIn.hasEarnedDailyXpFreezeOn(todayDayKey)
        val qualifiesDailyFreeze = !alreadyGrantedDaily && newDailyXp >= DAILY_XP_FREEZE_THRESHOLD
        val newGrantedDate = when {
            qualifiesDailyFreeze -> todayDayKey
            alreadyGrantedDaily -> todayDayKey
            else -> ""
        }

        // Daily Objectives evaluation & 3-day consecutive objective streak
        val prevRoundsToday = afterCheckIn.roundsCompletedOn(todayDayKey)
        val newRoundsToday = if (completedRound != null) prevRoundsToday + 1 else prevRoundsToday
        val todayScenarios = DailyObjectives.scenariosForDay(todayDayKey, userKey)
        val prevCompletedIds = afterCheckIn.completedObjectivesFor(todayDayKey)
        val newlyCompletedIds = if (completedRound != null) {
            val context = DailyObjectiveContext(
                result = completedRound,
                hintsUsed = hintsUsed.coerceAtLeast(0),
                dailyRoundsCompleted = newRoundsToday,
                dailyXpTotal = newDailyXp
            )
            todayScenarios
                .filter { it.id !in prevCompletedIds && it.isSatisfiedBy(context) }
                .map { it.id }
        } else {
            emptyList()
        }
        val mergedCompletedIds = (prevCompletedIds + newlyCompletedIds)
            .distinct()
            .filter { id -> todayScenarios.any { it.id == id } }
            .take(3)

        val allThreeCompletedNow = todayScenarios.all { it.id in mergedCompletedIds }
        val prevCompletedDay = afterCheckIn.lastObjectiveCompletedDay
        var nextObjectiveStreak = afterCheckIn.dailyObjectiveStreak.coerceAtLeast(0)
        var nextLastCompletedDay = prevCompletedDay
        var nextObjFreezeGrantedDate = afterCheckIn.objectiveFreezeGrantedDate
        var qualifiesObjectiveFreeze = false

        if (allThreeCompletedNow && prevCompletedDay != todayDayKey) {
            val gap = if (prevCompletedDay.isBlank()) -1 else daysBetweenDayKeys(prevCompletedDay, todayDayKey)
            val prevCyclePaidOut = prevCompletedDay.isNotBlank() && afterCheckIn.objectiveFreezeGrantedDate == prevCompletedDay
            nextObjectiveStreak = if (gap == 1 && !prevCyclePaidOut) {
                (nextObjectiveStreak + 1).coerceAtMost(OBJECTIVE_STREAK_TARGET_DAYS)
            } else {
                1
            }
            nextLastCompletedDay = todayDayKey
            if (nextObjectiveStreak >= OBJECTIVE_STREAK_TARGET_DAYS && nextObjFreezeGrantedDate != todayDayKey) {
                qualifiesObjectiveFreeze = true
                nextObjFreezeGrantedDate = todayDayKey
            }
        }

        val startFreezes = afterCheckIn.streakFreezes.coerceIn(0, MAX_STREAK_FREEZES)
        val fromLevelAdded = minOf(levelUps, MAX_STREAK_FREEZES - startFreezes)
        val afterLevelFreezes = startFreezes + fromLevelAdded
        val fromDailyAdded = if (qualifiesDailyFreeze && afterLevelFreezes < MAX_STREAK_FREEZES) 1 else 0
        val afterDailyFreezes = afterLevelFreezes + fromDailyAdded
        val fromObjectivesAdded = if (qualifiesObjectiveFreeze && afterDailyFreezes < MAX_STREAK_FREEZES) 1 else 0
        val finalFreezes = (afterDailyFreezes + fromObjectivesAdded).coerceIn(0, MAX_STREAK_FREEZES)

        val updated = afterCheckIn.copy(
            streakFreezes = finalFreezes,
            dailyXpEarned = newDailyXp,
            dailyXpDate = todayDayKey,
            dailyXpFreezeGrantedDate = newGrantedDate,
            objectiveDayKey = todayDayKey,
            completedObjectiveIds = mergedCompletedIds,
            dailyRoundsCompleted = newRoundsToday,
            dailyObjectiveStreak = nextObjectiveStreak,
            lastObjectiveCompletedDay = nextLastCompletedDay,
            objectiveFreezeGrantedDate = nextObjFreezeGrantedDate
        )
        return FreezeRewardOutcome(
            updated = updated,
            freezesEarnedFromLevelUp = fromLevelAdded,
            freezeEarnedFromDailyXp = fromDailyAdded > 0,
            freezeEarnedFromObjectives = fromObjectivesAdded > 0,
            newlyCompletedObjectiveIds = newlyCompletedIds
        )
    }
}
