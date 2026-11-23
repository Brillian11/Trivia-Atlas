package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Leaderboard
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.AvatarOption
import com.example.data.model.ProgressionSystem
import com.example.data.model.WeeklyRankingEntry
import com.example.ui.auth.GoogleSignInButton
import com.example.ui.components.AvatarEmblem
import com.example.ui.theme.LocalAtlasColors
import com.example.ui.viewmodel.LeaderboardSort
import com.example.ui.viewmodel.LeaderboardViewModel
import com.example.ui.viewmodel.UiState

@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel,
    currentUserId: String?,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.leaderboardState.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val remainingMillis by viewModel.remainingUntilResetMillis.collectAsStateWithLifecycle()
    val atlasColors = LocalAtlasColors.current
    var authError by remember { mutableStateOf<String?>(null) }

    val showResetCountdown = ProgressionSystem.isWithinResetCountdownWindow(remainingMillis)

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 620.dp)
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.leaderboard_title),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (showResetCountdown) {
                        val countdownText = formatResetCountdown(remainingMillis)
                        Spacer(Modifier.width(12.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.testTag("weekly_reset_countdown_pill")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Schedule,
                                    contentDescription = null,
                                    tint = atlasColors.gold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = countdownText,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.leaderboard_weekly_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!viewModel.isGuest) {
                    Spacer(Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(LeaderboardSort.entries) { option ->
                            FilterChip(
                                selected = sortOption == option,
                                onClick = { viewModel.setSortOption(option) },
                                label = { Text(stringResource(option.labelRes)) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.testTag("leaderboard_sort_${option.name.lowercase()}")
                            )
                        }
                    }
                }
            }

            if (viewModel.isGuest) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = atlasColors.cardElevated,
                        border = BorderStroke(1.dp, atlasColors.subtleBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.EmojiEvents,
                                contentDescription = null,
                                tint = atlasColors.gold,
                                modifier = Modifier.size(52.dp)
                            )
                            Spacer(Modifier.height(14.dp))
                            Text(
                                text = stringResource(R.string.leaderboard_guest_title),
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.leaderboard_guest_body),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (authError != null) {
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = authError!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Spacer(Modifier.height(18.dp))
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
                return@Column
            }

            when (val current = state) {
                is UiState.Loading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Error -> {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Leaderboard,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = current.message,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                is UiState.Success -> {
                    val entries = current.data
                    if (entries.isEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.EmojiEvents,
                                contentDescription = null,
                                tint = atlasColors.gold,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.weekly_no_ranks_yet),
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = stringResource(R.string.weekly_no_ranks_body),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        val myRankIndex = entries.indexOfFirst { it.userId == currentUserId }
                        val myEntry = if (myRankIndex >= 0) entries[myRankIndex] else null

                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (myEntry != null) {
                                item(key = "my_rank_card") {
                                    Surface(
                                        shape = MaterialTheme.shapes.large,
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Text(
                                                text = "#${myRankIndex + 1}",
                                                style = MaterialTheme.typography.headlineSmall,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Spacer(Modifier.width(12.dp))
                                            AvatarEmblem(
                                                avatar = AvatarOption.fromId(myEntry.avatarId),
                                                size = 40.dp
                                            )
                                            Spacer(Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Text(
                                                        text = stringResource(R.string.your_standing, myEntry.displayName),
                                                        style = MaterialTheme.typography.titleSmall,
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        modifier = Modifier.weight(1f, fill = false)
                                                    )
                                                    Surface(
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.14f),
                                                        shape = RoundedCornerShape(50)
                                                    ) {
                                                        Text(
                                                            text = stringResource(R.string.level_badge_short, myEntry.level.toInt().coerceAtLeast(1)),
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                                Text(
                                                    text = stringResource(
                                                        R.string.rounds_accuracy_line,
                                                        myEntry.gamesPlayed,
                                                        myEntry.accuracyPercent
                                                    ),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                                )
                                            }
                                            Text(
                                                text = metricValue(myEntry, sortOption),
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                }
                            } else {
                                item(key = "unranked_weekly_notice") {
                                    Surface(
                                        shape = MaterialTheme.shapes.medium,
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = stringResource(R.string.your_unranked_weekly),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                                        )
                                    }
                                }
                            }

                            if (entries.size >= 3) {
                                item(key = "podium_row") {
                                    PodiumShowcase(
                                        first = entries[0],
                                        second = entries[1],
                                        third = entries[2],
                                        sortOption = sortOption
                                    )
                                }
                            }

                            itemsIndexed(entries, key = { _, item -> item.userId }) { index, entry ->
                                val isCurrentUser = entry.userId == currentUserId
                                LeaderboardRowCard(
                                    rank = index + 1,
                                    entry = entry,
                                    sortOption = sortOption,
                                    isCurrentUser = isCurrentUser
                                )
                            }

                            item { Spacer(Modifier.height(16.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun formatResetCountdown(remainingMillis: Long): String {
    val totalSeconds = (remainingMillis / 1000L).coerceAtLeast(0L)
    val days = totalSeconds / 86_400L
    val hours = (totalSeconds % 86_400L) / 3_600L
    val minutes = (totalSeconds % 3_600L) / 60L
    val seconds = totalSeconds % 60L

    return when {
        days > 0L -> stringResource(R.string.ranking_resets_in_dh, days.toInt(), hours.toInt())
        hours > 0L -> stringResource(R.string.ranking_resets_in_hm, hours.toInt(), minutes.toInt())
        else -> stringResource(R.string.ranking_resets_in_ms, minutes.toInt(), seconds.toInt())
    }
}

@Composable
private fun PodiumShowcase(
    first: WeeklyRankingEntry,
    second: WeeklyRankingEntry,
    third: WeeklyRankingEntry,
    sortOption: LeaderboardSort
) {
    val atlasColors = LocalAtlasColors.current
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = atlasColors.cardElevated,
        border = BorderStroke(1.dp, atlasColors.subtleBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 18.dp)
        ) {
            PodiumColumn(rank = 2, entry = second, sortOption = sortOption, accent = Color(0xFF9EA7AD), avatarSize = 48.dp, modifier = Modifier.weight(1f))
            PodiumColumn(rank = 1, entry = first, sortOption = sortOption, accent = atlasColors.gold, avatarSize = 60.dp, modifier = Modifier.weight(1f))
            PodiumColumn(rank = 3, entry = third, sortOption = sortOption, accent = Color(0xFFB87A56), avatarSize = 46.dp, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun PodiumColumn(
    rank: Int,
    entry: WeeklyRankingEntry,
    sortOption: LeaderboardSort,
    accent: Color,
    avatarSize: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 4.dp)
    ) {
        Surface(
            color = accent.copy(alpha = 0.2f),
            shape = CircleShape
        ) {
            Text(
                text = "#$rank",
                style = MaterialTheme.typography.labelSmall,
                color = accent,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        AvatarEmblem(avatar = AvatarOption.fromId(entry.avatarId), size = avatarSize)
        Spacer(Modifier.height(8.dp))
        Text(
            text = entry.displayName,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.level_badge_short, entry.level.toInt().coerceAtLeast(1)),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = metricValue(entry, sortOption),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun LeaderboardRowCard(
    rank: Int,
    entry: WeeklyRankingEntry,
    sortOption: LeaderboardSort,
    isCurrentUser: Boolean
) {
    val atlasColors = LocalAtlasColors.current
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = if (isCurrentUser) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f) else atlasColors.cardElevated,
        border = BorderStroke(
            width = if (isCurrentUser) 1.5.dp else 1.dp,
            color = if (isCurrentUser) MaterialTheme.colorScheme.secondary else atlasColors.subtleBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(
                text = rank.toString().padStart(2, '0'),
                style = MaterialTheme.typography.labelMedium,
                color = if (rank <= 3) atlasColors.gold else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(30.dp)
            )
            AvatarEmblem(avatar = AvatarOption.fromId(entry.avatarId), size = 40.dp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = entry.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Surface(
                        color = atlasColors.goldContainer,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = stringResource(R.string.level_badge_short, entry.level.toInt().coerceAtLeast(1)),
                            style = MaterialTheme.typography.labelSmall,
                            color = atlasColors.onGoldContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "@${entry.handle} • ${entry.accuracyPercent}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = metricValue(entry, sortOption),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun metricValue(entry: WeeklyRankingEntry, sortOption: LeaderboardSort): String = when (sortOption) {
    LeaderboardSort.TOTAL_SCORE -> stringResource(R.string.xp_badge, ProgressionSystem.formatXp(entry.weeklyXp))
    LeaderboardSort.HIGH_SCORE -> stringResource(R.string.best_suffix, entry.highScore)
    LeaderboardSort.BEST_STREAK -> "${entry.bestStreak}x"
}
