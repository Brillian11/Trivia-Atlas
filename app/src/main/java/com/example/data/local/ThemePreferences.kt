package com.example.data.local

import android.content.Context
import android.content.res.Configuration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.ProgressionSystem
import com.example.data.model.QuizDifficulty
import com.example.data.model.QuizMode
import com.example.data.model.ThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "trivia_atlas_prefs")

object LanguageStorage {
    private const val PREFS_NAME = "trivia_atlas_lang_sync"
    private const val KEY_LANG = "app_language_code"

    fun getSavedLanguage(context: Context): AppLanguage {
        val raw = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANG, AppLanguage.SYSTEM.code)
        return AppLanguage.fromCode(raw)
    }

    fun saveLanguageSync(context: Context, language: AppLanguage) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANG, language.code)
            .commit()
    }

    fun effectiveLanguageCode(context: Context): String {
        val saved = getSavedLanguage(context)
        return if (saved == AppLanguage.SYSTEM) {
            val sysLang = context.resources.configuration.locales[0]?.language?.lowercase() ?: "en"
            when (sysLang) {
                "es" -> "es"
                "in", "id" -> "in"
                else -> "en"
            }
        } else {
            saved.code
        }
    }

    fun wrapContext(base: Context): Context {
        val saved = getSavedLanguage(base)
        if (saved == AppLanguage.SYSTEM) return base

        val locale = when (saved) {
            AppLanguage.SPANISH -> Locale("es")
            AppLanguage.INDONESIAN -> Locale("in", "ID")
            AppLanguage.ENGLISH -> Locale("en")
            AppLanguage.SYSTEM -> return base
        }
        Locale.setDefault(locale)
        val config = Configuration(base.resources.configuration).apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
        return base.createConfigurationContext(config)
    }
}

class ThemePreferences(context: Context) {
    private val appContext = context.applicationContext
    private val keyVault = LocalGeminiKeyVault(appContext)
    private val vaultScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val TIMER_ENABLED = booleanPreferencesKey("timer_enabled")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
        val PLAY_MODE = stringPreferencesKey("play_mode")
        val QUESTION_COUNT = intPreferencesKey("question_count")
        val DIFFICULTY = stringPreferencesKey("difficulty")
        val OFFLINE_MODE_ENABLED = booleanPreferencesKey("offline_mode_enabled")
        val LIBRARY_CLEARED = booleanPreferencesKey("library_cleared")
        val LEGACY_CUSTOM_GEMINI_API_KEY = stringPreferencesKey("custom_gemini_api_key")
        val CUSTOM_GEMINI_MODEL = stringPreferencesKey("custom_gemini_model")
        val CUSTOM_GEMINI_MODEL_NAME = stringPreferencesKey("custom_gemini_model_name")
        val GUEST_DAILY_STREAK = intPreferencesKey("guest_daily_streak")
        val GUEST_BEST_DAILY_STREAK = intPreferencesKey("guest_best_daily_streak")
        val GUEST_STREAK_FREEZES = intPreferencesKey("guest_streak_freezes")
        val GUEST_LAST_ACTIVE_DAY = stringPreferencesKey("guest_last_active_day")
        val GUEST_LAST_FREEZE_USED_DAY = stringPreferencesKey("guest_last_freeze_used_day")
        val GUEST_DAILY_XP_EARNED = longPreferencesKey("guest_daily_xp_earned")
        val GUEST_DAILY_XP_DATE = stringPreferencesKey("guest_daily_xp_date")
        val GUEST_DAILY_XP_FREEZE_GRANTED_DATE = stringPreferencesKey("guest_daily_xp_freeze_granted_date")
        val GUEST_OBJECTIVE_DAY_KEY = stringPreferencesKey("guest_objective_day_key")
        val GUEST_COMPLETED_OBJECTIVE_IDS = stringPreferencesKey("guest_completed_objective_ids")
        val GUEST_DAILY_ROUNDS_COMPLETED = intPreferencesKey("guest_daily_rounds_completed")
        val GUEST_DAILY_OBJECTIVE_STREAK = intPreferencesKey("guest_daily_objective_streak")
        val GUEST_LAST_OBJECTIVE_COMPLETED_DAY = stringPreferencesKey("guest_last_objective_completed_day")
        val GUEST_OBJECTIVE_FREEZE_GRANTED_DATE = stringPreferencesKey("guest_objective_freeze_granted_date")
        val GUEST_DEVICE_UUID = stringPreferencesKey("guest_device_uuid")
    }

    private val _customGeminiApiKey = MutableStateFlow(keyVault.getKey())
    val customGeminiApiKey: StateFlow<String?> = _customGeminiApiKey.asStateFlow()

    val customGeminiModelId: Flow<String?> = appContext.dataStore.data.map { prefs ->
        prefs[Keys.CUSTOM_GEMINI_MODEL]?.trim()?.takeIf { it.isNotEmpty() }
    }

    val customGeminiModelName: Flow<String?> = appContext.dataStore.data.map { prefs ->
        prefs[Keys.CUSTOM_GEMINI_MODEL_NAME]?.trim()?.takeIf { it.isNotEmpty() }
            ?: prefs[Keys.CUSTOM_GEMINI_MODEL]?.trim()?.takeIf { it.isNotEmpty() }
    }

    init {
        // Migrate any legacy plaintext key out of DataStore into hardware-backed LocalGeminiKeyVault
        vaultScope.launch {
            runCatching {
                val prefs = appContext.dataStore.data.first()
                val legacy = prefs[Keys.LEGACY_CUSTOM_GEMINI_API_KEY]?.trim()
                if (!legacy.isNullOrBlank()) {
                    if (!keyVault.hasKey()) {
                        keyVault.saveKey(legacy)
                        _customGeminiApiKey.value = legacy
                    }
                    appContext.dataStore.edit { mutable ->
                        mutable.remove(Keys.LEGACY_CUSTOM_GEMINI_API_KEY)
                    }
                }
            }
        }
    }

    val themeMode: Flow<ThemeMode> = appContext.dataStore.data.map { prefs ->
        ThemeMode.fromKey(prefs[Keys.THEME_MODE])
    }

    val timerEnabled: Flow<Boolean> = appContext.dataStore.data.map { prefs ->
        prefs[Keys.TIMER_ENABLED] ?: true
    }

    val appLanguage: Flow<AppLanguage> = appContext.dataStore.data.map { prefs ->
        val raw = prefs[Keys.APP_LANGUAGE]
        if (raw != null) AppLanguage.fromCode(raw) else LanguageStorage.getSavedLanguage(appContext)
    }

    val playMode: Flow<QuizMode> = appContext.dataStore.data.map { prefs ->
        val parsed = QuizMode.fromKey(prefs[Keys.PLAY_MODE])
        if (parsed == QuizMode.GUESS) QuizMode.GUESS else QuizMode.STANDARD
    }

    val questionCount: Flow<Int> = appContext.dataStore.data.map { prefs ->
        val stored = prefs[Keys.QUESTION_COUNT] ?: 10
        if (stored in listOf(5, 10, 15)) stored else 10
    }

    val difficulty: Flow<QuizDifficulty> = appContext.dataStore.data.map { prefs ->
        QuizDifficulty.fromKey(prefs[Keys.DIFFICULTY])
    }

    val offlineModeEnabled: Flow<Boolean> = appContext.dataStore.data.map { prefs ->
        prefs[Keys.OFFLINE_MODE_ENABLED] ?: false
    }

    val libraryCleared: Flow<Boolean> = appContext.dataStore.data.map { prefs ->
        prefs[Keys.LIBRARY_CLEARED] ?: false
    }

    val guestStreak: Flow<ProgressionSystem.StreakSnapshot> = appContext.dataStore.data.map { prefs ->
        prefs.toGuestStreakSnapshot()
    }

    val guestDeviceIdFlow: Flow<String> = appContext.dataStore.data.map { prefs ->
        prefs[Keys.GUEST_DEVICE_UUID].orEmpty()
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        appContext.dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode.key
        }
    }

    suspend fun setTimerEnabled(enabled: Boolean) {
        appContext.dataStore.edit { prefs ->
            prefs[Keys.TIMER_ENABLED] = enabled
        }
    }

    suspend fun setAppLanguage(language: AppLanguage) {
        LanguageStorage.saveLanguageSync(appContext, language)
        appContext.dataStore.edit { prefs ->
            prefs[Keys.APP_LANGUAGE] = language.code
        }
    }

    suspend fun setPlayMode(mode: QuizMode) {
        val normalized = if (mode == QuizMode.GUESS) QuizMode.GUESS else QuizMode.STANDARD
        appContext.dataStore.edit { prefs ->
            prefs[Keys.PLAY_MODE] = normalized.key
        }
    }

    suspend fun setQuestionCount(count: Int) {
        val normalized = if (count in listOf(5, 10, 15)) count else 10
        appContext.dataStore.edit { prefs ->
            prefs[Keys.QUESTION_COUNT] = normalized
        }
    }

    suspend fun setDifficulty(difficulty: QuizDifficulty) {
        appContext.dataStore.edit { prefs ->
            prefs[Keys.DIFFICULTY] = difficulty.apiKey
        }
    }

    suspend fun setOfflineModeEnabled(enabled: Boolean) {
        appContext.dataStore.edit { prefs ->
            prefs[Keys.OFFLINE_MODE_ENABLED] = enabled
        }
    }

    suspend fun setLibraryCleared(cleared: Boolean) {
        appContext.dataStore.edit { prefs ->
            prefs[Keys.LIBRARY_CLEARED] = cleared
        }
    }

    suspend fun setCustomGeminiApiKey(key: String?) {
        val cleaned = key?.trim()?.takeIf { it.isNotEmpty() }
        keyVault.saveKey(cleaned)
        _customGeminiApiKey.value = keyVault.getKey()
        appContext.dataStore.edit { prefs ->
            prefs.remove(Keys.LEGACY_CUSTOM_GEMINI_API_KEY)
            if (cleaned == null) {
                prefs.remove(Keys.CUSTOM_GEMINI_MODEL)
                prefs.remove(Keys.CUSTOM_GEMINI_MODEL_NAME)
            }
        }
    }

    suspend fun setCustomGeminiModel(modelId: String?, displayName: String? = null) {
        val cleanedId = modelId?.trim()?.takeIf { it.isNotEmpty() }
        val cleanedName = displayName?.trim()?.takeIf { it.isNotEmpty() }
        appContext.dataStore.edit { prefs ->
            if (cleanedId == null) {
                prefs.remove(Keys.CUSTOM_GEMINI_MODEL)
                prefs.remove(Keys.CUSTOM_GEMINI_MODEL_NAME)
            } else {
                prefs[Keys.CUSTOM_GEMINI_MODEL] = cleanedId
                if (cleanedName != null) {
                    prefs[Keys.CUSTOM_GEMINI_MODEL_NAME] = cleanedName
                } else {
                    prefs.remove(Keys.CUSTOM_GEMINI_MODEL_NAME)
                }
            }
        }
    }

    suspend fun getOrCreateGuestDeviceId(): String {
        var resolved: String? = null
        appContext.dataStore.edit { prefs ->
            val existing = prefs[Keys.GUEST_DEVICE_UUID]
            if (!existing.isNullOrBlank()) {
                resolved = existing
            } else {
                val created = java.util.UUID.randomUUID().toString()
                prefs[Keys.GUEST_DEVICE_UUID] = created
                resolved = created
            }
        }
        return resolved ?: "guest_default"
    }

    fun getDailyWikiCacheJson(userScopeKey: String, wikiLang: String): String? {
        val prefs = appContext.getSharedPreferences(WIKI_CACHE_PREFS, Context.MODE_PRIVATE)
        return prefs.getString("carousel_v2_${userScopeKey}_$wikiLang", null)
    }

    fun saveDailyWikiCacheJson(userScopeKey: String, wikiLang: String, json: String) {
        appContext.getSharedPreferences(WIKI_CACHE_PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString("carousel_v2_${userScopeKey}_$wikiLang", json)
            .commit()
    }

    fun getSeenWikiPageIds(userScopeKey: String): Set<Long> {
        val prefs = appContext.getSharedPreferences(WIKI_CACHE_PREFS, Context.MODE_PRIVATE)
        val raw = prefs.getStringSet("seen_$userScopeKey", emptySet()).orEmpty()
        return raw.mapNotNull { it.toLongOrNull() }.toSet()
    }

    fun addSeenWikiPageIds(userScopeKey: String, pageIds: List<Long>) {
        val prefs = appContext.getSharedPreferences(WIKI_CACHE_PREFS, Context.MODE_PRIVATE)
        val updated = (getSeenWikiPageIds(userScopeKey) + pageIds).toList().takeLast(120)
        prefs.edit()
            .putStringSet("seen_$userScopeKey", updated.map { it.toString() }.toSet())
            .apply()
    }

    suspend fun recordGuestDailyCheckIn(): ProgressionSystem.DailyCheckInOutcome {
        val todayKey = ProgressionSystem.localDayKey()
        var resolvedOutcome: ProgressionSystem.DailyCheckInOutcome? = null
        appContext.dataStore.edit { prefs ->
            if (prefs[Keys.GUEST_DEVICE_UUID].isNullOrBlank()) {
                prefs[Keys.GUEST_DEVICE_UUID] = java.util.UUID.randomUUID().toString()
            }
            val current = prefs.toGuestStreakSnapshot()
            val outcome = ProgressionSystem.applyDailyCheckIn(current, todayKey)
            if (outcome.updated != current) {
                prefs.writeGuestStreakSnapshot(outcome.updated)
            }
            resolvedOutcome = outcome
        }
        return resolvedOutcome ?: ProgressionSystem.DailyCheckInOutcome(
            updated = ProgressionSystem.StreakSnapshot(dailyStreak = 1, bestDailyStreak = 1, lastActiveDay = todayKey, dailyXpDate = todayKey),
            checkedInToday = true
        )
    }

    suspend fun recordGuestXpForStreakFreezes(
        earnedXp: Long,
        prevLifetimeXp: Long,
        completedRound: com.example.data.model.QuizRoundResult? = null,
        hintsUsed: Int = 0
    ): ProgressionSystem.FreezeRewardOutcome {
        val todayKey = ProgressionSystem.localDayKey()
        var resolvedOutcome: ProgressionSystem.FreezeRewardOutcome? = null
        appContext.dataStore.edit { prefs ->
            val guestUserKey = prefs[Keys.GUEST_DEVICE_UUID]?.takeIf { it.isNotBlank() }
                ?: java.util.UUID.randomUUID().toString().also { prefs[Keys.GUEST_DEVICE_UUID] = it }
            val current = prefs.toGuestStreakSnapshot()
            val outcome = ProgressionSystem.applyXpForStreakFreezes(
                current = current,
                earnedXp = earnedXp,
                prevLifetimeXp = prevLifetimeXp,
                todayDayKey = todayKey,
                completedRound = completedRound,
                hintsUsed = hintsUsed,
                userKey = guestUserKey
            )
            prefs.writeGuestStreakSnapshot(outcome.updated)
            resolvedOutcome = outcome
        }
        return resolvedOutcome ?: ProgressionSystem.FreezeRewardOutcome(
            updated = ProgressionSystem.StreakSnapshot(dailyStreak = 1, bestDailyStreak = 1, lastActiveDay = todayKey, dailyXpDate = todayKey, objectiveDayKey = todayKey)
        )
    }

    private fun Preferences.toGuestStreakSnapshot(): ProgressionSystem.StreakSnapshot {
        val completedIdsRaw = this[Keys.GUEST_COMPLETED_OBJECTIVE_IDS].orEmpty()
        val parsedIds = if (completedIdsRaw.isBlank()) {
            emptyList()
        } else {
            completedIdsRaw.split(",").map { it.trim() }.filter { it.isNotEmpty() }.distinct().take(3)
        }
        return ProgressionSystem.StreakSnapshot(
            dailyStreak = (this[Keys.GUEST_DAILY_STREAK] ?: 0).coerceAtLeast(0),
            bestDailyStreak = (this[Keys.GUEST_BEST_DAILY_STREAK] ?: 0).coerceAtLeast(0),
            streakFreezes = (this[Keys.GUEST_STREAK_FREEZES] ?: 0)
                .coerceIn(0, ProgressionSystem.MAX_STREAK_FREEZES),
            lastActiveDay = this[Keys.GUEST_LAST_ACTIVE_DAY].orEmpty(),
            lastFreezeUsedDay = this[Keys.GUEST_LAST_FREEZE_USED_DAY].orEmpty(),
            dailyXpEarned = (this[Keys.GUEST_DAILY_XP_EARNED] ?: 0L).coerceAtLeast(0L),
            dailyXpDate = this[Keys.GUEST_DAILY_XP_DATE].orEmpty(),
            dailyXpFreezeGrantedDate = this[Keys.GUEST_DAILY_XP_FREEZE_GRANTED_DATE].orEmpty(),
            objectiveDayKey = this[Keys.GUEST_OBJECTIVE_DAY_KEY].orEmpty(),
            completedObjectiveIds = parsedIds,
            dailyRoundsCompleted = (this[Keys.GUEST_DAILY_ROUNDS_COMPLETED] ?: 0).coerceAtLeast(0),
            dailyObjectiveStreak = (this[Keys.GUEST_DAILY_OBJECTIVE_STREAK] ?: 0).coerceAtLeast(0),
            lastObjectiveCompletedDay = this[Keys.GUEST_LAST_OBJECTIVE_COMPLETED_DAY].orEmpty(),
            objectiveFreezeGrantedDate = this[Keys.GUEST_OBJECTIVE_FREEZE_GRANTED_DATE].orEmpty()
        )
    }

    private fun MutablePreferences.writeGuestStreakSnapshot(snapshot: ProgressionSystem.StreakSnapshot) {
        this[Keys.GUEST_DAILY_STREAK] = snapshot.dailyStreak.coerceAtLeast(0)
        this[Keys.GUEST_BEST_DAILY_STREAK] = snapshot.bestDailyStreak.coerceAtLeast(0)
        this[Keys.GUEST_STREAK_FREEZES] = snapshot.streakFreezes.coerceIn(0, ProgressionSystem.MAX_STREAK_FREEZES)
        this[Keys.GUEST_LAST_ACTIVE_DAY] = snapshot.lastActiveDay
        this[Keys.GUEST_LAST_FREEZE_USED_DAY] = snapshot.lastFreezeUsedDay
        this[Keys.GUEST_DAILY_XP_EARNED] = snapshot.dailyXpEarned.coerceAtLeast(0L)
        this[Keys.GUEST_DAILY_XP_DATE] = snapshot.dailyXpDate
        this[Keys.GUEST_DAILY_XP_FREEZE_GRANTED_DATE] = snapshot.dailyXpFreezeGrantedDate
        this[Keys.GUEST_OBJECTIVE_DAY_KEY] = snapshot.objectiveDayKey
        this[Keys.GUEST_COMPLETED_OBJECTIVE_IDS] = snapshot.completedObjectiveIds.distinct().take(3).joinToString(",")
        this[Keys.GUEST_DAILY_ROUNDS_COMPLETED] = snapshot.dailyRoundsCompleted.coerceAtLeast(0)
        this[Keys.GUEST_DAILY_OBJECTIVE_STREAK] = snapshot.dailyObjectiveStreak.coerceAtLeast(0)
        this[Keys.GUEST_LAST_OBJECTIVE_COMPLETED_DAY] = snapshot.lastObjectiveCompletedDay
        this[Keys.GUEST_OBJECTIVE_FREEZE_GRANTED_DATE] = snapshot.objectiveFreezeGrantedDate
    }

    private companion object {
        const val WIKI_CACHE_PREFS = "trivia_atlas_wiki_daily"
    }
}
