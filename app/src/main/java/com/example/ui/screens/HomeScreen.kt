package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.ImageSearch
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.example.R
import com.example.data.model.AvatarOption
import com.example.data.model.DailyObjectiveProgressItem
import com.example.data.model.ObjectiveLaunchSpec
import com.example.data.model.ProgressionSystem
import com.example.data.model.QuizDifficulty
import com.example.data.model.QuizMode
import com.example.data.model.TriviaCategory
import com.example.data.model.WikipediaArticle
import com.example.ui.components.AvatarEmblem
import com.example.ui.components.CategoryIllustrationBadge
import com.example.ui.theme.LocalAtlasColors
import com.example.ui.viewmodel.HomeViewModel
import com.example.ui.viewmodel.UiState

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartQuiz: (TriviaCategory, QuizDifficulty, QuizMode, Int) -> Unit,
    onOpenWikipediaArticle: (WikipediaArticle) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCached by viewModel.totalCachedCount.collectAsStateWithLifecycle()
    val pictureCount by viewModel.pictureQuestionCount.collectAsStateWithLifecycle()
    val categoryCounts by viewModel.categoryCounts.collectAsStateWithLifecycle()
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val guestStats by viewModel.guestStats.collectAsStateWithLifecycle()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsStateWithLifecycle()
    val selectedPlayMode by viewModel.selectedPlayMode.collectAsStateWithLifecycle()
    val selectedCount by viewModel.selectedQuestionCount.collectAsStateWithLifecycle()
    val wikiCarouselState by viewModel.wikiCarouselState.collectAsStateWithLifecycle()
    val wikiSearchQuery by viewModel.wikiSearchQuery.collectAsStateWithLifecycle()
    val wikiSearchState by viewModel.wikiSearchState.collectAsStateWithLifecycle()
    val customUrlLoading by viewModel.customUrlLoading.collectAsStateWithLifecycle()
    val customUrlError by viewModel.customUrlError.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()
    val streakSnapshot by viewModel.streakSnapshot.collectAsStateWithLifecycle()
    val dailyObjectives by viewModel.dailyObjectives.collectAsStateWithLifecycle()
    val todayDayKey = remember { ProgressionSystem.localDayKey() }
    val objectiveStreakDays = remember(streakSnapshot, todayDayKey) {
        streakSnapshot.effectiveObjectiveStreak(todayDayKey)
    }

    var showCustomUrlDialog by remember { mutableStateOf(false) }
    var customUrlInput by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.onAppOpened()
        viewModel.ensureDailyRefresh()
    }

    if (showCustomUrlDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!customUrlLoading) {
                    showCustomUrlDialog = false
                    viewModel.clearCustomUrlError()
                }
            },
            title = { Text(stringResource(R.string.wiki_custom_url_dialog_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = stringResource(R.string.wiki_custom_url_dialog_sub),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = customUrlInput,
                        onValueChange = {
                            customUrlInput = it
                            if (customUrlError != null) viewModel.clearCustomUrlError()
                        },
                        label = { Text(stringResource(R.string.wiki_custom_url_input_label)) },
                        placeholder = { Text("https://en.wikipedia.org/wiki/...") },
                        singleLine = true,
                        enabled = !customUrlLoading,
                        isError = customUrlError != null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Go,
                            autoCorrectEnabled = false
                        ),
                        keyboardActions = KeyboardActions(
                            onGo = {
                                if (customUrlInput.isNotBlank() && !customUrlLoading) {
                                    viewModel.openCustomArticleUrl(customUrlInput) { article ->
                                        showCustomUrlDialog = false
                                        customUrlInput = ""
                                        onOpenWikipediaArticle(article)
                                    }
                                }
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("wiki_custom_url_input")
                    )
                    if (customUrlLoading) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Text(
                                text = stringResource(R.string.wiki_custom_url_loading),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (customUrlError != null) {
                        val errText = when (customUrlError) {
                            "INVALID_URL" -> stringResource(R.string.wiki_custom_url_error_invalid)
                            "OFFLINE" -> stringResource(R.string.wiki_offline_msg)
                            else -> stringResource(R.string.wiki_custom_url_error_unreadable)
                        }
                        Text(
                            text = errText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.testTag("wiki_custom_url_error_text")
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.openCustomArticleUrl(customUrlInput) { article ->
                            showCustomUrlDialog = false
                            customUrlInput = ""
                            onOpenWikipediaArticle(article)
                        }
                    },
                    enabled = customUrlInput.isNotBlank() && !customUrlLoading,
                    modifier = Modifier.testTag("wiki_custom_url_open_button")
                ) {
                    Text(stringResource(R.string.wiki_custom_url_open_button))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCustomUrlDialog = false
                        viewModel.clearCustomUrlError()
                    },
                    enabled = !customUrlLoading
                ) {
                    Text(stringResource(R.string.cancel_button))
                }
            },
            modifier = Modifier.testTag("wiki_custom_url_dialog")
        )
    }

    val profile = (profileState as? UiState.Success)?.data
    val avatar = AvatarOption.fromId(profile?.avatarId)
    val guestName = stringResource(R.string.guest_explorer)
    val displayName = if (viewModel.isGuest) guestName else (profile?.displayName ?: guestName)
    val headerPoints = if (viewModel.isGuest) guestStats.totalScore else (profile?.totalScore ?: 0L)
    val headerAccuracy = if (viewModel.isGuest) guestStats.accuracyPercent else (profile?.accuracyPercent ?: 0)
    val levelProgress = remember(headerPoints) { ProgressionSystem.calculateLevelProgress(headerPoints) }
    val atlasColors = LocalAtlasColors.current

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val columns = if (maxWidth >= 680.dp) 3 else 2

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Minimalist Editorial Header (Profile + Optional Sign In)
            item(span = { GridItemSpan(columns) }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(onClick = onOpenProfile)
                            .padding(vertical = 4.dp, horizontal = 2.dp)
                            .testTag("home_profile_header")
                    ) {
                        AvatarEmblem(avatar = avatar, size = 44.dp)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f, fill = false)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.welcome_user, displayName),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                Surface(
                                    color = atlasColors.goldContainer,
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.testTag("home_level_badge")
                                ) {
                                    Text(
                                        text = stringResource(R.string.level_badge_short, levelProgress.level),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = atlasColors.onGoldContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.testTag("home_streak_badge")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.LocalFireDepartment,
                                            contentDescription = stringResource(R.string.daily_streak_title),
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "${streakSnapshot.dailyStreak}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }
                            Text(
                                text = stringResource(
                                    R.string.home_level_summary,
                                    levelProgress.level,
                                    ProgressionSystem.formatXp(headerPoints),
                                    headerAccuracy
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    if (viewModel.isGuest) {
                        Surface(
                            onClick = onOpenSignIn,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.testTag("home_sign_in_chip")
                        ) {
                            Text(
                                text = stringResource(R.string.sign_in_short),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }

            // Optional status banner
            if (statusBanner != null) {
                item(span = { GridItemSpan(columns) }) {
                    val resolvedBanner = when {
                        statusBanner!!.startsWith("STREAK_FROZEN_SAVED:") -> {
                            val parts = statusBanner!!.split(":")
                            val streakDays = parts.getOrNull(1)?.toIntOrNull() ?: streakSnapshot.dailyStreak
                            val freezesLeft = parts.getOrNull(2)?.toIntOrNull() ?: streakSnapshot.streakFreezes
                            stringResource(R.string.streak_frozen_saved_banner, streakDays, freezesLeft)
                        }
                        statusBanner!!.startsWith("STREAK_EXTENDED:") -> {
                            val streakDays = statusBanner!!.substringAfter("STREAK_EXTENDED:").toIntOrNull()
                                ?: streakSnapshot.dailyStreak
                            stringResource(R.string.streak_checked_in_banner, streakDays)
                        }
                        statusBanner!!.startsWith("+") -> {
                            val added = statusBanner!!.drop(1).toIntOrNull() ?: 0
                            stringResource(R.string.added_questions_banner, added)
                        }
                        statusBanner == "UP_TO_DATE" -> stringResource(R.string.cache_up_to_date)
                        statusBanner == "SYNC_ERROR" -> stringResource(R.string.unable_sync_pack)
                        else -> statusBanner!!
                    }
                    AnimatedVisibility(visible = true) {
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
                                    text = resolvedBanner,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = viewModel::dismissBanner,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Random Daily Wikipedia Article Carousel + Hero Expedition Controls
            item(span = { GridItemSpan(columns) }) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = atlasColors.cardElevated,
                    border = BorderStroke(1.dp, atlasColors.subtleBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        WikipediaSearchAndUrlBar(
                            query = wikiSearchQuery,
                            onQueryChange = viewModel::onWikiSearchQueryChanged,
                            onSubmitSearch = viewModel::submitWikipediaSearch,
                            onClearSearch = viewModel::clearWikiSearch,
                            onOpenUrlDialog = {
                                viewModel.clearCustomUrlError()
                                showCustomUrlDialog = true
                            }
                        )

                        if (wikiSearchQuery.trim().length >= 2 && wikiSearchState != null) {
                            WikipediaSearchResultsSection(
                                query = wikiSearchQuery.trim(),
                                searchState = wikiSearchState!!,
                                onSelectArticle = { article ->
                                    viewModel.openSearchedArticle(article)
                                    onOpenWikipediaArticle(article)
                                },
                                onRetry = viewModel::submitWikipediaSearch
                            )
                        } else {
                            DailyWikipediaCarouselSection(
                                wikiCarouselState = wikiCarouselState,
                                onOpenArticle = onOpenWikipediaArticle,
                                onRetry = { viewModel.refreshWikipediaCarousel(forceRefresh = false) }
                            )
                        }

                        HorizontalDivider(
                            color = atlasColors.subtleBorder,
                            modifier = Modifier.padding(horizontal = 18.dp)
                        )

                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (viewModel.isGuest) {
                                    Surface(
                                        color = atlasColors.goldContainer,
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.guest_mode_pill),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = atlasColors.onGoldContainer,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            if (viewModel.isGuest) {
                                Spacer(Modifier.height(8.dp))
                            }

                            Text(
                                text = stringResource(R.string.hero_title),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.hero_subtitle, totalCached, pictureCount),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    onStartQuiz(
                                        TriviaCategory.MIXED,
                                        selectedDifficulty,
                                        selectedPlayMode,
                                        selectedCount
                                    )
                                },
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("start_atlas_mix_button")
                            ) {
                                Icon(
                                    imageVector = if (selectedPlayMode == QuizMode.GUESS) Icons.Rounded.EditNote else Icons.Rounded.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    if (selectedPlayMode == QuizMode.GUESS) {
                                        stringResource(R.string.play_atlas_mix_guess)
                                    } else {
                                        stringResource(R.string.mode_daily_mix)
                                    }
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                FilledTonalButton(
                                    onClick = {
                                        onStartQuiz(
                                            TriviaCategory.MIXED,
                                            selectedDifficulty,
                                            QuizMode.GUESS,
                                            selectedCount
                                        )
                                    },
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("start_guess_mode_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.EditNote,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = stringResource(R.string.mode_guess),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                FilledTonalButton(
                                    onClick = {
                                        onStartQuiz(
                                            TriviaCategory.PICTURE_ATLAS,
                                            QuizDifficulty.ALL,
                                            QuizMode.PICTURE,
                                            minOf(selectedCount, 12)
                                        )
                                    },
                                    shape = MaterialTheme.shapes.medium,
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("start_picture_quiz_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ImageSearch,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = stringResource(R.string.mode_picture_quiz),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2.5 Today's 3 Daily Objectives (Explore Only, Tap-to-Launch)
            item(span = { GridItemSpan(columns) }) {
                DailyObjectivesSection(
                    objectives = dailyObjectives,
                    objectiveStreakDays = objectiveStreakDays,
                    onLaunchObjective = { scenario ->
                        when (val spec = scenario.launchSpec) {
                            is ObjectiveLaunchSpec.Quiz -> {
                                onStartQuiz(
                                    spec.category,
                                    spec.difficulty,
                                    spec.mode,
                                    spec.questionCount
                                )
                            }
                            ObjectiveLaunchSpec.FeaturedWikipedia -> {
                                val featuredArticle = (wikiCarouselState as? UiState.Success)?.data?.firstOrNull()
                                if (featuredArticle != null) {
                                    onOpenWikipediaArticle(featuredArticle)
                                } else {
                                    viewModel.refreshWikipediaCarousel(forceRefresh = false)
                                }
                            }
                        }
                    }
                )
            }

            // 3. Section Header
            item(span = { GridItemSpan(columns) }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.categories_header),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${stringResource(R.string.question_count_chip, selectedCount)} • ${
                            stringResource(if (selectedPlayMode == QuizMode.GUESS) R.string.tap_to_guess else R.string.tap_for_quiz)
                        }",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable(onClick = onOpenProfile)
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }
            }

            // 4. Illustrated Category Cards
            items(TriviaCategory.playableGridCategories, key = { it.apiKey }) { category ->
                val cachedInCat = categoryCounts[category.apiKey] ?: 0
                Surface(
                    onClick = {
                        onStartQuiz(
                            category,
                            selectedDifficulty,
                            selectedPlayMode,
                            selectedCount
                        )
                    },
                    shape = MaterialTheme.shapes.large,
                    color = atlasColors.cardElevated,
                    border = BorderStroke(1.dp, atlasColors.subtleBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("category_card_${category.apiKey}")
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CategoryIllustrationBadge(category = category, size = 44.dp)
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = "$cachedInCat",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        Text(
                            text = stringResource(category.titleRes),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = stringResource(category.subtitleRes),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Picture Atlas shortcut card
            item(span = { GridItemSpan(columns) }) {
                OutlinedButton(
                    onClick = {
                        onStartQuiz(
                            TriviaCategory.PICTURE_ATLAS,
                            QuizDifficulty.ALL,
                            if (selectedPlayMode == QuizMode.GUESS) QuizMode.GUESS else QuizMode.PICTURE,
                            minOf(selectedCount, 12)
                        )
                    },
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("picture_guess_shortcut_button")
                ) {
                    Icon(Icons.Rounded.ImageSearch, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        stringResource(
                            if (selectedPlayMode == QuizMode.GUESS) {
                                R.string.play_picture_guess
                            } else {
                                R.string.explore_picture_plates
                            }
                        )
                    )
                }
            }

            item(span = { GridItemSpan(columns) }) {
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun WikipediaSearchAndUrlBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmitSearch: () -> Unit,
    onClearSearch: () -> Unit,
    onOpenUrlDialog: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            placeholder = {
                Text(
                    text = stringResource(R.string.wiki_search_hint),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = onClearSearch,
                        modifier = Modifier.testTag("wiki_search_clear_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(R.string.wiki_search_clear_cd)
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onSubmitSearch() }
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .weight(1f)
                .testTag("wiki_search_input")
        )

        FilledTonalIconButton(
            onClick = onOpenUrlDialog,
            shape = RoundedCornerShape(16.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier
                .size(54.dp)
                .testTag("wiki_custom_url_button")
        ) {
            Icon(
                imageVector = Icons.Rounded.Link,
                contentDescription = stringResource(R.string.wiki_open_url_cd)
            )
        }
    }
}

@Composable
private fun WikipediaSearchResultsSection(
    query: String,
    searchState: UiState<List<WikipediaArticle>>,
    onSelectArticle: (WikipediaArticle) -> Unit,
    onRetry: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 14.dp, bottom = 14.dp)
    ) {
        when (searchState) {
            is UiState.Loading -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.5.dp)
                    Text(
                        text = stringResource(R.string.wiki_searching),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            is UiState.Error -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (searchState.message == "OFFLINE") {
                            stringResource(R.string.wiki_offline_msg)
                        } else {
                            stringResource(R.string.wiki_search_error)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onRetry) {
                        Text(stringResource(R.string.retry_button))
                    }
                }
            }

            is UiState.Success -> {
                val results = searchState.data
                if (results.isEmpty()) {
                    Text(
                        text = stringResource(R.string.wiki_search_empty, query),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
                    )
                } else {
                    results.forEachIndexed { idx, article ->
                        Surface(
                            onClick = { onSelectArticle(article) },
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, LocalAtlasColors.current.subtleBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("wiki_search_result_$idx")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(10.dp)
                            ) {
                                if (!article.thumbnailUrl.isNullOrBlank()) {
                                    SubcomposeAsyncImage(
                                        model = article.thumbnailUrl,
                                        contentDescription = article.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                    )
                                } else {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.size(52.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Rounded.Public,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = article.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    val sub = article.description?.takeIf { it.isNotBlank() } ?: article.extract
                                    if (sub.isNotBlank()) {
                                        Text(
                                            text = sub,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                    contentDescription = stringResource(R.string.wiki_read_and_quiz_cta),
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyWikipediaCarouselSection(
    wikiCarouselState: UiState<List<WikipediaArticle>>,
    onOpenArticle: (WikipediaArticle) -> Unit,
    onRetry: () -> Unit
) {
    when (wikiCarouselState) {
        is UiState.Loading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 10f)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(modifier = Modifier.size(30.dp), strokeWidth = 3.dp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.wiki_loading),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        is UiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9.5f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_atlas),
                    contentDescription = stringResource(R.string.cat_wikipedia_title),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.28f),
                                    Color.Black.copy(alpha = 0.78f)
                                )
                            )
                        )
                )
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp)
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Public,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.cat_wikipedia_title),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (wikiCarouselState.message == "OFFLINE") {
                                stringResource(R.string.wiki_offline_msg)
                            } else {
                                stringResource(R.string.wiki_error_msg)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        FilledTonalButton(
                            onClick = onRetry,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("wiki_carousel_retry_button")
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(stringResource(R.string.retry_button))
                        }
                    }
                }
            }
        }

        is UiState.Success -> {
            val articles = wikiCarouselState.data
            if (articles.isEmpty()) return
            val pagerState = rememberPagerState(pageCount = { articles.size })
            val activeArticle = articles[pagerState.currentPage.coerceIn(0, articles.lastIndex)]

            Column(modifier = Modifier.fillMaxWidth()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("wiki_article_pager")
                ) { page ->
                    val article = articles[page]
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 10f)
                            .clickable { onOpenArticle(article) }
                            .testTag("wiki_carousel_card_$page")
                    ) {
                        if (!article.thumbnailUrl.isNullOrBlank()) {
                            SubcomposeAsyncImage(
                                model = article.thumbnailUrl,
                                contentDescription = article.title,
                                contentScale = ContentScale.Crop,
                                loading = {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(26.dp), strokeWidth = 2.5.dp)
                                    }
                                },
                                error = {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_hero_atlas),
                                        contentDescription = article.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.img_hero_atlas),
                                contentDescription = article.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.18f),
                                            Color.Black.copy(alpha = 0.35f),
                                            Color.Black.copy(alpha = 0.88f)
                                        )
                                    )
                                )
                        )

                        Column(
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    color = Color.Black.copy(alpha = 0.62f),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Public,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(Modifier.width(5.dp))
                                        Text(
                                            text = stringResource(
                                                R.string.wiki_carousel_badge,
                                                article.wikiLang.uppercase()
                                            ),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White
                                        )
                                    }
                                }

                                Surface(
                                    color = Color.Black.copy(alpha = 0.62f),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = stringResource(
                                            R.string.wiki_article_counter,
                                            page + 1,
                                            articles.size
                                        ),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }

                            Column {
                                if (!article.description.isNullOrBlank()) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.92f),
                                        shape = RoundedCornerShape(50),
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    ) {
                                        Text(
                                            text = article.description,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = article.title,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = article.extract,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.88f),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // Carousel Dots + Article Quiz Action Bar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        articles.indices.forEach { idx ->
                            val selected = pagerState.currentPage == idx
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.5.dp)
                                    .height(7.dp)
                                    .width(if (selected) 20.dp else 7.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (selected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.outlineVariant
                                        }
                                    )
                            )
                        }
                    }

                    FilledTonalButton(
                        onClick = { onOpenArticle(activeArticle) },
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("start_wiki_article_quiz_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.wiki_read_and_quiz_cta),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyObjectivesSection(
    objectives: List<DailyObjectiveProgressItem>,
    objectiveStreakDays: Int,
    onLaunchObjective: (com.example.data.model.DailyObjectiveScenario) -> Unit
) {
    val atlasColors = LocalAtlasColors.current
    val completedCount = objectives.count { it.completed }
    val targetDays = ProgressionSystem.OBJECTIVE_STREAK_TARGET_DAYS
    val safeStreak = objectiveStreakDays.coerceIn(0, targetDays)

    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = atlasColors.cardElevated,
        border = BorderStroke(1.dp, atlasColors.subtleBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("home_daily_objectives_card")
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = atlasColors.goldContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.Flag,
                                contentDescription = null,
                                tint = atlasColors.gold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = stringResource(R.string.daily_objectives_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(
                                if (safeStreak >= targetDays) {
                                    R.string.daily_objectives_streak_complete_pill
                                } else {
                                    R.string.daily_objectives_streak_pill
                                },
                                safeStreak,
                                targetDays
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (safeStreak >= targetDays) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        repeat(targetDays) { idx ->
                            val reached = idx < safeStreak
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (reached) atlasColors.gold else MaterialTheme.colorScheme.outlineVariant
                                    )
                            )
                        }
                    }
                    Surface(
                        color = if (completedCount == objectives.size && objectives.isNotEmpty()) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.testTag("daily_objectives_count_pill")
                    ) {
                        Text(
                            text = stringResource(
                                R.string.daily_objectives_progress_pill,
                                completedCount,
                                objectives.size
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (completedCount == objectives.size && objectives.isNotEmpty()) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            objectives.forEachIndexed { index, item ->
                val isDone = item.completed
                val containerColor = if (isDone) {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.48f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                }

                Surface(
                    onClick = { onLaunchObjective(item.scenario) },
                    enabled = !isDone,
                    shape = MaterialTheme.shapes.medium,
                    color = containerColor,
                    border = if (isDone) {
                        BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("daily_objective_item_$index")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = if (isDone) Icons.Rounded.CheckCircle else Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            tint = if (isDone) MaterialTheme.colorScheme.primary else atlasColors.gold,
                            modifier = Modifier.size(20.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(item.scenario.titleRes),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isDone) FontWeight.Medium else FontWeight.SemiBold
                                ),
                                color = if (isDone) {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            val cur = item.currentNumericProgress
                            val target = item.targetNumericProgress
                            if (!isDone && cur != null && target != null && cur > 0) {
                                Text(
                                    text = "$cur / $target",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Surface(
                            color = if (isDone) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                            } else {
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.72f)
                            },
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = if (isDone) {
                                    stringResource(R.string.daily_objective_done_badge)
                                } else {
                                    stringResource(item.scenario.badgeRes)
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isDone) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (!isDone) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = stringResource(R.string.daily_objective_launch_cd),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

