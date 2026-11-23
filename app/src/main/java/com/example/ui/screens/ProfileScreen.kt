package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.BuildConfig
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.AvatarOption
import com.example.data.model.ProgressionSystem
import com.example.data.model.QuizDifficulty
import com.example.data.model.QuizMode
import com.example.data.model.QuizSession
import com.example.data.model.ThemeMode
import com.example.data.model.TriviaCategory
import com.example.data.model.UserProfile
import com.example.ui.auth.GoogleSignInButton
import com.example.ui.auth.signOutUser
import com.example.ui.components.AvatarEmblem
import com.example.ui.components.CategoryIllustrationBadge
import com.example.ui.theme.LocalAtlasColors
import com.example.ui.viewmodel.ProfileViewModel
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLanguageApplied: () -> Unit,
    onAuthSuccess: () -> Unit,
    onSignedOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val sessionsState by viewModel.sessionsState.collectAsStateWithLifecycle()
    val guestStats by viewModel.guestStats.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val timerEnabled by viewModel.timerEnabled.collectAsStateWithLifecycle()
    val selectedPlayMode by viewModel.selectedPlayMode.collectAsStateWithLifecycle()
    val selectedCount by viewModel.selectedQuestionCount.collectAsStateWithLifecycle()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsStateWithLifecycle()
    val totalCached by viewModel.totalCachedCount.collectAsStateWithLifecycle()
    val pendingCount by viewModel.pendingScoreCount.collectAsStateWithLifecycle()
    val offlineModeEnabled by viewModel.offlineModeEnabled.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val feedback by viewModel.feedbackMessage.collectAsStateWithLifecycle()
    val customApiKey by viewModel.customGeminiApiKey.collectAsStateWithLifecycle()
    val customGeminiModelId by viewModel.customGeminiModelId.collectAsStateWithLifecycle()
    val customGeminiModelName by viewModel.customGeminiModelName.collectAsStateWithLifecycle()
    val availableGeminiModelsState by viewModel.availableGeminiModelsState.collectAsStateWithLifecycle()
    val weeklyRanking by viewModel.weeklyRankingState.collectAsStateWithLifecycle()
    val streakSnapshot by viewModel.streakSnapshot.collectAsStateWithLifecycle()
    val todayDayKey = remember { ProgressionSystem.localDayKey() }

    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val atlasColors = LocalAtlasColors.current

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteLibraryDialog by remember { mutableStateOf(false) }
    var showStreakRulesDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }

    val profile = (profileState as? UiState.Success)?.data ?: UserProfile()
    val profileUpdatedMessage = stringResource(R.string.profile_updated)

    if (showEditDialog && !viewModel.isGuest) {
        EditProfileDialog(
            initialProfile = profile,
            isSaving = isSaving,
            onDismiss = { showEditDialog = false },
            onSave = { name, handle, avatar, bio, favCat ->
                viewModel.saveProfile(
                    displayName = name,
                    handle = handle,
                    avatarOption = avatar,
                    bio = bio,
                    favoriteCategory = favCat,
                    successMessage = profileUpdatedMessage,
                    onSaved = { showEditDialog = false }
                )
            }
        )
    }

    if (showDeleteLibraryDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteLibraryDialog = false },
            title = { Text(stringResource(R.string.delete_library_confirm_title)) },
            text = { Text(stringResource(R.string.delete_library_confirm_msg, totalCached)) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteLibraryDialog = false
                        viewModel.deleteAllLibrary()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.testTag("confirm_delete_library_button")
                ) {
                    Text(stringResource(R.string.confirm_delete_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteLibraryDialog = false }) {
                    Text(stringResource(R.string.cancel_button))
                }
            }
        )
    }

    if (showStreakRulesDialog) {
        AlertDialog(
            onDismissRequest = { showStreakRulesDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.AcUnit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text(stringResource(R.string.streak_rules_dialog_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.streak_rules_intro),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider(color = atlasColors.subtleBorder)
                    Text(
                        text = stringResource(R.string.streak_rule_xp_10k),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.streak_rule_daily_objectives),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.streak_rule_level_up),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showStreakRulesDialog = false },
                    modifier = Modifier.testTag("streak_rules_dialog_close")
                ) {
                    Text(stringResource(R.string.battle_rules_got_it))
                }
            },
            modifier = Modifier.testTag("daily_streak_info_dialog")
        )
    }

    if (showAboutDialog) {
        AboutTriviaAtlasDialog(
            onDismiss = { showAboutDialog = false },
            onOpenRepository = { repoUrl ->
                runCatching { uriHandler.openUri(repoUrl) }
            }
        )
    }

    val shownDisplayName = if (viewModel.isGuest) stringResource(R.string.guest_explorer) else profile.displayName
    val shownHandle = if (viewModel.isGuest) stringResource(R.string.guest_handle) else profile.handle
    val shownBio = if (viewModel.isGuest) stringResource(R.string.guest_bio) else profile.bio
    val shownTotalScore = if (viewModel.isGuest) guestStats.totalScore else profile.totalScore
    val shownHighScore = if (viewModel.isGuest) guestStats.highScore else profile.highScore
    val shownAccuracy = if (viewModel.isGuest) guestStats.accuracyPercent else profile.accuracyPercent
    val shownGames = if (viewModel.isGuest) guestStats.gamesPlayed else profile.gamesPlayed
    val shownStreak = if (viewModel.isGuest) guestStats.bestStreak else profile.bestStreak
    val levelProgress = remember(shownTotalScore) {
        ProgressionSystem.calculateLevelProgress(shownTotalScore)
    }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .widthIn(max = 620.dp)
                .fillMaxSize()
        ) {
            item {
                Text(
                    text = stringResource(R.string.profile_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (feedback != null) {
                item {
                    val resolvedFeedback = when {
                        feedback!!.startsWith("+") -> {
                            val added = feedback!!.drop(1).toIntOrNull() ?: 0
                            stringResource(R.string.added_questions_banner, added)
                        }
                        feedback!!.startsWith("REFRESHED:") -> {
                            val total = feedback!!.substringAfter("REFRESHED:").toIntOrNull() ?: totalCached
                            stringResource(R.string.feedback_library_refreshed, total)
                        }
                        feedback == "OFFLINE_MODE_ON" -> stringResource(R.string.feedback_offline_mode_on)
                        feedback == "OFFLINE_MODE_OFF" -> stringResource(R.string.feedback_offline_mode_off)
                        feedback == "LIBRARY_DELETED" -> stringResource(R.string.feedback_library_deleted)
                        feedback == "CUSTOM_KEY_SAVED" -> stringResource(R.string.custom_api_key_saved)
                        feedback == "UP_TO_DATE" -> stringResource(R.string.cache_up_to_date)
                        feedback == "SYNC_ERROR" -> stringResource(R.string.unable_sync_pack)
                        else -> feedback!!
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = resolvedFeedback,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = viewModel::clearFeedback) {
                                Text(stringResource(R.string.ok_button))
                            }
                        }
                    }
                }
            }

            // Profile Card
            item {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = atlasColors.cardElevated,
                    border = BorderStroke(1.dp, atlasColors.subtleBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AvatarEmblem(
                                avatar = AvatarOption.fromId(profile.avatarId),
                                size = 64.dp
                            )
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = shownDisplayName,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "@$shownHandle",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (!viewModel.isGuest) {
                                    Spacer(Modifier.height(4.dp))
                                    val favCat = TriviaCategory.fromApiKey(profile.favoriteCategory)
                                    Text(
                                        text = stringResource(R.string.specialty_label, stringResource(favCat.titleRes)),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        if (shownBio.isNotBlank()) {
                            Spacer(Modifier.height(14.dp))
                            Text(
                                text = shownBio,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (!viewModel.isGuest) {
                            Spacer(Modifier.height(16.dp))
                            OutlinedButton(
                                onClick = { showEditDialog = true },
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("edit_profile_button")
                            ) {
                                Icon(Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.edit_profile))
                            }
                        }
                    }
                }
            }

            // Optional Sign-In card for Guest users
            if (viewModel.isGuest) {
                item {
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = stringResource(R.string.guest_sync_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.guest_sync_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (authError != null) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = authError!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            GoogleSignInButton(
                                onAuthSuccess = {
                                    authError = null
                                    onAuthSuccess()
                                },
                                onAuthError = { authError = it }
                            )
                        }
                    }
                }
            }

            // Lifetime Explorer Level Card
            item {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = atlasColors.cardElevated,
                    border = BorderStroke(1.dp, atlasColors.subtleBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_lifetime_level_card")
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.level_title_card),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                color = atlasColors.goldContainer,
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.testTag("profile_level_badge")
                            ) {
                                Text(
                                    text = stringResource(R.string.level_badge_short, levelProgress.level),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = atlasColors.onGoldContainer,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { levelProgress.progressFraction },
                            color = atlasColors.gold,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = StrokeCap.Round,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(50))
                                .testTag("profile_level_progress_bar")
                        )

                        Text(
                            text = stringResource(
                                R.string.level_xp_progress,
                                ProgressionSystem.formatXp(levelProgress.xpIntoCurrentLevel),
                                ProgressionSystem.formatXp(levelProgress.xpRequiredForCurrentLevel),
                                levelProgress.level + 1
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Daily Streak & Streak Freezes Card
            item {
                val dailyXpToday = streakSnapshot.dailyXpFor(todayDayKey)
                val xpFraction = (dailyXpToday.toFloat() / ProgressionSystem.DAILY_XP_FREEZE_THRESHOLD.toFloat())
                    .coerceIn(0f, 1f)
                val savedToday = streakSnapshot.lastFreezeUsedDay == todayDayKey

                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = atlasColors.cardElevated,
                    border = BorderStroke(1.dp, atlasColors.subtleBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_daily_streak_card")
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Rounded.LocalFireDepartment,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.daily_streak_title),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        IconButton(
                                            onClick = { showStreakRulesDialog = true },
                                            modifier = Modifier
                                                .size(48.dp)
                                                .testTag("daily_streak_info_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Info,
                                                contentDescription = stringResource(R.string.streak_rules_info_cd),
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = stringResource(R.string.daily_streak_best, streakSnapshot.bestDailyStreak),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.testTag("profile_daily_streak_pill")
                            ) {
                                Text(
                                    text = stringResource(R.string.daily_streak_days_count, streakSnapshot.dailyStreak),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                        }

                        if (savedToday) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.AcUnit,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.streak_freeze_saved_today),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = atlasColors.subtleBorder)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.streak_freeze_title),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(
                                    R.string.streak_freeze_count_ratio,
                                    streakSnapshot.streakFreezes,
                                    ProgressionSystem.MAX_STREAK_FREEZES
                                ),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            repeat(ProgressionSystem.MAX_STREAK_FREEZES) { index ->
                                val filled = index < streakSnapshot.streakFreezes
                                Surface(
                                    color = if (filled) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    shape = MaterialTheme.shapes.medium,
                                    border = if (filled) {
                                        BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))
                                    } else null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Rounded.AcUnit,
                                            contentDescription = stringResource(R.string.streak_freeze_title),
                                            tint = if (filled) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                                            },
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }

                        LinearProgressIndicator(
                            progress = { xpFraction },
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = StrokeCap.Round,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(50))
                                .testTag("profile_daily_xp_progress_bar")
                        )

                        Text(
                            text = if (streakSnapshot.streakFreezes >= ProgressionSystem.MAX_STREAK_FREEZES) {
                                stringResource(R.string.streak_freeze_full_note)
                            } else {
                                stringResource(
                                    R.string.streak_daily_xp_progress,
                                    ProgressionSystem.formatXp(dailyXpToday),
                                    ProgressionSystem.formatXp(ProgressionSystem.DAILY_XP_FREEZE_THRESHOLD)
                                )
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = stringResource(R.string.streak_how_to_earn),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Progress Stats Grid
            item {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = atlasColors.cardElevated,
                    border = BorderStroke(1.dp, atlasColors.subtleBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = stringResource(R.string.personal_progress),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(14.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ProgressMetric(
                                stringResource(R.string.metric_lifetime_xp),
                                ProgressionSystem.formatXp(shownTotalScore),
                                Modifier.weight(1f)
                            )
                            ProgressMetric(stringResource(R.string.metric_high_score), "$shownHighScore", Modifier.weight(1f))
                            ProgressMetric(stringResource(R.string.metric_accuracy), "$shownAccuracy%", Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ProgressMetric(stringResource(R.string.metric_rounds), "$shownGames", Modifier.weight(1f))
                            ProgressMetric(stringResource(R.string.metric_best_streak), "${shownStreak}x", Modifier.weight(1f))
                            ProgressMetric(stringResource(R.string.metric_cached_qs), "$totalCached", Modifier.weight(1f))
                        }

                        if (!viewModel.isGuest) {
                            Spacer(Modifier.height(14.dp))
                            HorizontalDivider(color = atlasColors.subtleBorder)
                            Spacer(Modifier.height(14.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("profile_weekly_ranking_section")
                            ) {
                                Text(
                                    text = stringResource(R.string.weekly_ranking_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = stringResource(R.string.ranking_reset_schedule),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                ProgressMetric(
                                    stringResource(R.string.metric_weekly_xp),
                                    ProgressionSystem.formatXp(weeklyRanking?.weeklyXp ?: 0L),
                                    Modifier.weight(1f)
                                )
                                ProgressMetric(
                                    stringResource(R.string.metric_high_score),
                                    "${weeklyRanking?.highScore ?: 0L}",
                                    Modifier.weight(1f)
                                )
                                ProgressMetric(
                                    stringResource(R.string.metric_weekly_rounds),
                                    "${weeklyRanking?.gamesPlayed ?: 0L}",
                                    Modifier.weight(1f)
                                )
                            }
                        }

                        if (pendingCount > 0) {
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = stringResource(R.string.pending_sync_notice, pendingCount),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            // Category Play Mode Card
            item {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = atlasColors.cardElevated,
                    border = BorderStroke(1.dp, atlasColors.subtleBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.category_play_mode),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(5, 10, 15).forEach { count ->
                                    val selected = selectedCount == count
                                    FilterChip(
                                        selected = selected,
                                        onClick = { viewModel.selectQuestionCount(count) },
                                        label = {
                                            Text(
                                                text = stringResource(R.string.question_count_chip, count),
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        ),
                                        modifier = Modifier.testTag("count_chip_$count")
                                    )
                                }
                            }
                        }

                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            val modes = listOf(QuizMode.STANDARD, QuizMode.GUESS)
                            modes.forEachIndexed { index, mode ->
                                SegmentedButton(
                                    selected = selectedPlayMode == mode,
                                    onClick = { viewModel.selectPlayMode(mode) },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = modes.size
                                    ),
                                    modifier = Modifier.testTag("profile_mode_${mode.key}")
                                ) {
                                    Text(
                                        text = stringResource(
                                            if (mode == QuizMode.GUESS) R.string.mode_guess_full else R.string.mode_multiple_choice_full
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = stringResource(R.string.difficulty_label),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                QuizDifficulty.entries.forEach { diff ->
                                    FilterChip(
                                        selected = selectedDifficulty == diff,
                                        onClick = { viewModel.selectDifficulty(diff) },
                                        label = { Text(stringResource(diff.labelRes)) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        modifier = Modifier.testTag("difficulty_chip_${diff.apiKey}")
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Appearance, Language & Play Card
            item {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = atlasColors.cardElevated,
                    border = BorderStroke(1.dp, atlasColors.subtleBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = stringResource(R.string.appearance_and_play),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.theme_mode_label),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))

                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            ThemeMode.entries.forEachIndexed { index, mode ->
                                SegmentedButton(
                                    selected = themeMode == mode,
                                    onClick = { viewModel.setThemeMode(mode) },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = ThemeMode.entries.size
                                    ),
                                    modifier = Modifier.testTag("theme_mode_${mode.key}")
                                ) {
                                    Text(
                                        text = stringResource(mode.labelRes),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = stringResource(R.string.language_label),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))

                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            AppLanguage.entries.forEachIndexed { index, lang ->
                                SegmentedButton(
                                    selected = appLanguage == lang,
                                    onClick = {
                                        if (appLanguage != lang) {
                                            viewModel.setAppLanguage(lang, onLanguageApplied)
                                        }
                                    },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = AppLanguage.entries.size
                                    ),
                                    modifier = Modifier.testTag("language_mode_${lang.code}")
                                ) {
                                    Text(
                                        text = if (lang == AppLanguage.INDONESIAN) "ID" else stringResource(lang.labelRes),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.countdown_timer_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = stringResource(R.string.countdown_timer_sub),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = timerEnabled,
                                onCheckedChange = viewModel::setTimerEnabled,
                                modifier = Modifier.testTag("timer_switch")
                            )
                        }
                    }
                }
            }

            // Offline Mode & Library Controls Card
            item {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = atlasColors.cardElevated,
                    border = BorderStroke(1.dp, atlasColors.subtleBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.offline_library_count, totalCached),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = if (pendingCount > 0) {
                                    stringResource(R.string.pending_rounds_queued, pendingCount)
                                } else {
                                    stringResource(R.string.prefetch_api_hint)
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 12.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.offline_mode_toggle_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = stringResource(R.string.offline_mode_toggle_sub),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = offlineModeEnabled,
                                onCheckedChange = viewModel::setOfflineModeEnabled,
                                modifier = Modifier.testTag("offline_mode_switch")
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FilledTonalButton(
                                onClick = viewModel::refreshLibrary,
                                enabled = !isSyncing,
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("refresh_library_button")
                            ) {
                                if (isSyncing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Rounded.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = stringResource(
                                        if (isSyncing) R.string.syncing_button else R.string.refresh_library_button
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            OutlinedButton(
                                onClick = { showDeleteLibraryDialog = true },
                                enabled = !isSyncing && totalCached > 0,
                                shape = MaterialTheme.shapes.medium,
                                border = BorderStroke(
                                    1.dp,
                                    if (totalCached > 0) {
                                        MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                                    } else {
                                        atlasColors.subtleBorder
                                    }
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("delete_library_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.DeleteOutline,
                                    contentDescription = null,
                                    tint = if (totalCached > 0) {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.delete_library_button),
                                    color = if (totalCached > 0) {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // Custom Gemini API Key & Model Selection Card (Strictly On-Device)
            item {
                val hasSavedKey = !customApiKey.isNullOrBlank()
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = atlasColors.cardElevated,
                    border = BorderStroke(1.dp, atlasColors.subtleBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Never load the saved key back into UI text state; clear immediately once saved
                    var apiKeyInput by remember { mutableStateOf("") }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.custom_api_key_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.testTag("custom_api_key_device_badge")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Lock,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.custom_api_key_device_only_badge),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }

                        Text(
                            text = stringResource(R.string.custom_api_key_sub),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (hasSavedKey) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("custom_api_key_hidden_status")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Lock,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.custom_api_key_saved_hidden),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = apiKeyInput,
                            onValueChange = { apiKeyInput = it },
                            label = {
                                Text(
                                    stringResource(
                                        if (hasSavedKey) R.string.custom_api_key_replace_hint else R.string.custom_api_key_hint
                                    )
                                )
                            },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                autoCorrectEnabled = false
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_api_key_input")
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    viewModel.setCustomGeminiApiKey(apiKeyInput)
                                    apiKeyInput = ""
                                },
                                enabled = apiKeyInput.trim().isNotBlank(),
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("save_api_key_button")
                            ) {
                                Text(stringResource(R.string.save_button))
                            }
                            if (hasSavedKey) {
                                OutlinedButton(
                                    onClick = {
                                        apiKeyInput = ""
                                        viewModel.setCustomGeminiApiKey(null)
                                    },
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("clear_api_key_button")
                                ) {
                                    Text(stringResource(R.string.custom_api_key_clear_button))
                                }
                            }
                        }

                        HorizontalDivider(color = atlasColors.subtleBorder)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = stringResource(R.string.custom_gemini_model_label),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (hasSavedKey) {
                                IconButton(
                                    onClick = viewModel::refreshAvailableGeminiModels,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .testTag("refresh_gemini_models_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Refresh,
                                        contentDescription = stringResource(R.string.gemini_model_refresh_cd),
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        var modelDropdownExpanded by remember { mutableStateOf(false) }
                        val availableModels = (availableGeminiModelsState as? UiState.Success)?.data.orEmpty()
                        val isLoadingModels = availableGeminiModelsState is UiState.Loading
                        val modelFetchError = (availableGeminiModelsState as? UiState.Error)?.message
                        val selectedModelObj = availableModels.firstOrNull { it.modelId == customGeminiModelId }
                        val dropdownDisplayText = when {
                            !hasSavedKey -> stringResource(R.string.gemini_model_need_key)
                            isLoadingModels -> stringResource(R.string.gemini_model_loading)
                            modelFetchError != null -> stringResource(R.string.gemini_model_error_short)
                            availableModels.isEmpty() -> stringResource(R.string.gemini_model_none_found)
                            selectedModelObj != null -> selectedModelObj.displayName
                            !customGeminiModelName.isNullOrBlank() -> customGeminiModelName!!
                            !customGeminiModelId.isNullOrBlank() -> customGeminiModelId!!
                            else -> availableModels.first().displayName
                        }
                        val dropdownCanExpand = hasSavedKey && availableModels.isNotEmpty()

                        ExposedDropdownMenuBox(
                            expanded = modelDropdownExpanded && dropdownCanExpand,
                            onExpandedChange = { wantExpand ->
                                if (dropdownCanExpand) {
                                    modelDropdownExpanded = wantExpand
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = dropdownDisplayText,
                                onValueChange = {},
                                readOnly = true,
                                enabled = dropdownCanExpand,
                                singleLine = true,
                                trailingIcon = {
                                    if (isLoadingModels) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = modelDropdownExpanded && dropdownCanExpand
                                        )
                                    }
                                },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                modifier = Modifier
                                    .menuAnchor(
                                        type = MenuAnchorType.PrimaryNotEditable,
                                        enabled = dropdownCanExpand
                                    )
                                    .fillMaxWidth()
                                    .testTag("gemini_model_dropdown_field")
                            )

                            ExposedDropdownMenu(
                                expanded = modelDropdownExpanded && dropdownCanExpand,
                                onDismissRequest = { modelDropdownExpanded = false }
                            ) {
                                availableModels.forEach { model ->
                                    val isSelected = model.modelId == (selectedModelObj?.modelId ?: customGeminiModelId)
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(
                                                    text = model.displayName,
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                                Text(
                                                    text = model.modelId,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        trailingIcon = {
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Rounded.CheckCircle,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        },
                                        onClick = {
                                            viewModel.selectCustomGeminiModel(model)
                                            modelDropdownExpanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                        modifier = Modifier.testTag("gemini_model_option_${model.modelId}")
                                    )
                                }
                            }
                        }

                        if (hasSavedKey && modelFetchError != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (modelFetchError == "OFFLINE") {
                                        stringResource(R.string.wiki_offline_msg)
                                    } else if (modelFetchError == "NO_MODELS_AVAILABLE") {
                                        stringResource(R.string.gemini_model_none_found)
                                    } else {
                                        modelFetchError
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(
                                    onClick = viewModel::refreshAvailableGeminiModels,
                                    modifier = Modifier.testTag("retry_gemini_models_button")
                                ) {
                                    Text(stringResource(R.string.retry_button))
                                }
                            }
                        }
                    }
                }
            }

            // Recent Expeditions
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.recent_expeditions),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            when (val sess = sessionsState) {
                is UiState.Loading -> {
                    item {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(28.dp))
                        }
                    }
                }
                is UiState.Error -> {
                    item {
                        Text(
                            text = sess.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is UiState.Success -> {
                    if (sess.data.isEmpty()) {
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = stringResource(R.string.no_sessions_yet),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    } else {
                        items(sess.data, key = { it.id }) { session ->
                            SessionHistoryCard(session = session)
                        }
                    }
                }
            }

            // Sign Out (only when signed in)
            if (!viewModel.isGuest) {
                item {
                    Spacer(Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = {
                            signOutUser(
                                credentialManager = credentialManager,
                                scope = scope,
                                onSignOutComplete = onSignedOut
                            )
                        },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("sign_out_button")
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.sign_out))
                    }
                }
            }

            // About Section at the end of Profile page
            item {
                val repoUrl = stringResource(R.string.about_repository_url)
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = atlasColors.cardElevated,
                    border = BorderStroke(1.dp, atlasColors.subtleBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_about_card")
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = stringResource(R.string.about_section_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = stringResource(R.string.about_version_badge, BuildConfig.VERSION_NAME),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = stringResource(R.string.about_summary_body),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        HorizontalDivider(color = atlasColors.subtleBorder)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = stringResource(R.string.about_developer_label),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = stringResource(R.string.about_developer_name),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.testTag("about_developer_name")
                            )
                        }

                        Surface(
                            onClick = { runCatching { uriHandler.openUri(repoUrl) } },
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                                .testTag("about_repo_link_card")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Code,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.about_repository_label),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = repoUrl,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = MaterialTheme.colorScheme.primary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                                    contentDescription = stringResource(R.string.about_open_github_cd),
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        FilledTonalButton(
                            onClick = { showAboutDialog = true },
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("profile_about_open_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Key,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.about_open_window_cta),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun ProgressMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SessionHistoryCard(session: QuizSession) {
    val atlasColors = LocalAtlasColors.current
    val cat = TriviaCategory.fromApiKey(session.category)
    val modeLabel = when (session.mode) {
        QuizMode.GUESS.key -> " • ${stringResource(R.string.mode_guess)}"
        QuizMode.PICTURE.key -> " • ${stringResource(R.string.mode_picture_quiz)}"
        else -> ""
    }
    val offlineSuffix = if (session.playedOffline) " • ${stringResource(R.string.offline_badge)}" else ""

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = atlasColors.cardElevated,
        border = BorderStroke(1.dp, atlasColors.subtleBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            CategoryIllustrationBadge(category = cat, size = 38.dp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(cat.titleRes),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(
                        R.string.session_summary_line,
                        session.correctCount,
                        session.totalQuestions,
                        session.bestStreak,
                        "$modeLabel$offlineSuffix"
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = stringResource(R.string.pts_badge, session.score),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditProfileDialog(
    initialProfile: UserProfile,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, AvatarOption, String, TriviaCategory) -> Unit
) {
    var displayName by remember { mutableStateOf(initialProfile.displayName) }
    var handle by remember { mutableStateOf(initialProfile.handle) }
    var bio by remember { mutableStateOf(initialProfile.bio) }
    var selectedAvatar by remember { mutableStateOf(AvatarOption.fromId(initialProfile.avatarId)) }
    var selectedCategory by remember { mutableStateOf(TriviaCategory.fromApiKey(initialProfile.favoriteCategory)) }

    val cleanName = displayName.trim()
    val cleanHandle = handle.trim().removePrefix("@")
    val isValid = cleanName.length in 2..30 && cleanHandle.length in 2..24 && bio.trim().length <= 140

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.customize_profile)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(R.string.choose_emblem),
                    style = MaterialTheme.typography.labelLarge
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AvatarOption.entries.forEach { avatar ->
                        val active = selectedAvatar == avatar
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                width = if (active) 2.dp else 1.dp,
                                color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier
                                .clickable { selectedAvatar = avatar }
                                .testTag("avatar_option_${avatar.id}")
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                AvatarEmblem(avatar = avatar, size = 40.dp)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = stringResource(avatar.labelRes),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = displayName,
                    onValueChange = { if (it.length <= 30) displayName = it },
                    label = { Text(stringResource(R.string.display_name_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("profile_name_input")
                )

                OutlinedTextField(
                    value = handle,
                    onValueChange = { if (it.length <= 24) handle = it },
                    label = { Text(stringResource(R.string.handle_hint)) },
                    prefix = { Text("@") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("profile_handle_input")
                )

                OutlinedTextField(
                    value = bio,
                    onValueChange = { if (it.length <= 140) bio = it },
                    label = { Text(stringResource(R.string.bio_hint)) },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("profile_bio_input")
                )

                Text(
                    text = stringResource(R.string.favorite_category_label),
                    style = MaterialTheme.typography.labelLarge
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TriviaCategory.playableGridCategories.take(6).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(stringResource(cat.titleRes)) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(cleanName, cleanHandle, selectedAvatar, bio.trim(), selectedCategory)
                },
                enabled = isValid && !isSaving,
                modifier = Modifier.testTag("save_profile_button")
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.save_button))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_button))
            }
        }
    )
}

@Composable
private fun AboutTriviaAtlasDialog(
    onDismiss: () -> Unit,
    onOpenRepository: (String) -> Unit
) {
    val atlasColors = LocalAtlasColors.current
    val repoUrl = stringResource(R.string.about_repository_url)

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.about_dialog_title),
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${stringResource(R.string.about_version_badge, BuildConfig.VERSION_NAME)} • ${stringResource(R.string.about_developer_name)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Developer & Repository Banner
                Surface(
                    onClick = { onOpenRepository(repoUrl) },
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .testTag("about_dialog_repo_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Code,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${stringResource(R.string.about_developer_label)}: ${stringResource(R.string.about_developer_name)}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = repoUrl,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                            contentDescription = stringResource(R.string.about_open_github_cd),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.about_apis_section),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                AboutDetailCard(
                    title = stringResource(R.string.about_api_trivia_title),
                    description = stringResource(R.string.about_api_trivia_desc),
                    subtleBorder = atlasColors.subtleBorder
                )
                AboutDetailCard(
                    title = stringResource(R.string.about_api_wiki_title),
                    description = stringResource(R.string.about_api_wiki_desc),
                    subtleBorder = atlasColors.subtleBorder
                )
                AboutDetailCard(
                    title = stringResource(R.string.about_api_gemini_title),
                    description = stringResource(R.string.about_api_gemini_desc),
                    subtleBorder = atlasColors.subtleBorder
                )
                AboutDetailCard(
                    title = stringResource(R.string.about_api_firebase_title),
                    description = stringResource(R.string.about_api_firebase_desc),
                    subtleBorder = atlasColors.subtleBorder
                )

                HorizontalDivider(color = atlasColors.subtleBorder)

                Text(
                    text = stringResource(R.string.about_repos_section),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.about_repos_list),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = stringResource(R.string.about_license_footer),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("about_dialog_close_button")
            ) {
                Text(stringResource(R.string.close_button))
            }
        },
        modifier = Modifier.testTag("about_app_dialog")
    )
}

@Composable
private fun AboutDetailCard(
    title: String,
    description: String,
    subtleBorder: androidx.compose.ui.graphics.Color
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, subtleBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

