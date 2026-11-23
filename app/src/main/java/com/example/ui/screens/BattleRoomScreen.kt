package com.example.ui.screens

import android.content.Intent
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.LanguageStorage
import com.example.data.model.AppLanguage
import com.example.data.model.AvatarOption
import com.example.data.model.BattleInvite
import com.example.data.model.BattleInviteStatus
import com.example.data.model.BattleParticipant
import com.example.data.model.BattleRoom
import com.example.data.model.BattleStatus
import com.example.data.model.LeaderboardEntry
import com.example.data.model.QuestionReviewItem
import com.example.data.model.QuizMode
import com.example.ui.components.AvatarEmblem
import com.example.ui.components.CategoryIllustrationBadge
import com.example.ui.components.PictureQuizMediaFrame
import com.example.ui.theme.LocalAtlasColors
import com.example.ui.viewmodel.BattlePlayState
import com.example.ui.viewmodel.BattleRoomViewModel
import com.example.ui.viewmodel.UiState
import java.util.Locale

@Composable
fun BattleRoomScreen(
    viewModel: BattleRoomViewModel,
    onExit: () -> Unit
) {
    val roomState by viewModel.roomState.collectAsStateWithLifecycle()
    val participants by viewModel.participants.collectAsStateWithLifecycle()
    val explorers by viewModel.explorers.collectAsStateWithLifecycle()
    val roomInvites by viewModel.roomInvites.collectAsStateWithLifecycle()
    val playState by viewModel.playState.collectAsStateWithLifecycle()
    val actionBusy by viewModel.actionBusy.collectAsStateWithLifecycle()
    val invitingUserId by viewModel.invitingUserId.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.feedbackMessage.collectAsStateWithLifecycle()

    var showLeaveDialog by remember { mutableStateOf(false) }
    var showRulesDialog by remember { mutableStateOf(false) }

    if (showRulesDialog) {
        BattleRulesInfoDialog(onDismiss = { showRulesDialog = false })
    }

    val room = (roomState as? UiState.Success)?.data
    val isHost = room != null && room.hostUserId == viewModel.currentUserId
    val shouldConfirmLeave = room != null && (
        room.battleStatus == BattleStatus.WAITING ||
            (room.battleStatus == BattleStatus.ACTIVE && playState?.completed != true)
        )

    BackHandler(enabled = shouldConfirmLeave) {
        showLeaveDialog = true
    }

    if (showLeaveDialog && room != null) {
        val messageRes = when {
            room.battleStatus == BattleStatus.WAITING && isHost -> R.string.battle_leave_room_host_msg
            room.battleStatus == BattleStatus.WAITING -> R.string.battle_leave_room_player_msg
            else -> R.string.battle_leave_active_msg
        }
        AlertDialog(
            onDismissRequest = { showLeaveDialog = false },
            title = { Text(stringResource(R.string.battle_leave_room_title)) },
            text = { Text(stringResource(messageRes)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLeaveDialog = false
                        if (room.battleStatus == BattleStatus.WAITING) {
                            viewModel.leaveWaitingRoom(onExit)
                        } else {
                            onExit()
                        }
                    }
                ) {
                    Text(stringResource(R.string.leave_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveDialog = false }) {
                    Text(stringResource(R.string.keep_playing_button))
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val currentRoomState = roomState) {
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
                            .widthIn(max = 460.dp)
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = currentRoomState.message,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onExit) {
                            Text(stringResource(R.string.back_button))
                        }
                    }
                }

                is UiState.Success -> {
                    val activeRoom = currentRoomState.data
                    if (activeRoom == null) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .widthIn(max = 460.dp)
                                .fillMaxSize()
                                .padding(24.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.battle_room_closed),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = onExit) {
                                Text(stringResource(R.string.back_button))
                            }
                        }
                    } else if (activeRoom.battleStatus == BattleStatus.WAITING) {
                        BattleLobbyContent(
                            room = activeRoom,
                            currentUserId = viewModel.currentUserId,
                            participants = participants,
                            explorers = explorers,
                            roomInvites = roomInvites,
                            actionBusy = actionBusy,
                            invitingUserId = invitingUserId,
                            feedbackMessage = feedbackMessage,
                            onDismissFeedback = viewModel::clearFeedback,
                            onBackRequest = { showLeaveDialog = true },
                            onShowRules = { showRulesDialog = true },
                            onInviteFriend = viewModel::sendDirectInvite,
                            onStartBattle = viewModel::startBattle
                        )
                    } else {
                        val play = playState
                        if (play == null) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                CircularProgressIndicator()
                            }
                        } else if (!play.completed && activeRoom.battleStatus == BattleStatus.ACTIVE) {
                            BattleArenaContent(
                                room = activeRoom,
                                playState = play,
                                participants = participants,
                                currentUserId = viewModel.currentUserId,
                                onBackRequest = { showLeaveDialog = true },
                                onShowRules = { showRulesDialog = true },
                                onSelectOption = viewModel::selectAnswer,
                                onGuessInputChange = viewModel::updateGuessInput,
                                onRevealHint = viewModel::revealHint,
                                onSubmitGuess = viewModel::submitGuess,
                                onSkipGuess = viewModel::skipGuess,
                                onNext = viewModel::advanceToNext
                            )
                        } else {
                            BattlePodiumContent(
                                room = activeRoom,
                                playState = play,
                                participants = participants,
                                currentUserId = viewModel.currentUserId,
                                actionBusy = actionBusy,
                                onShowRules = { showRulesDialog = true },
                                onFinishRoomNow = viewModel::markRoomFinished,
                                onBackToArena = onExit
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BattleLobbyContent(
    room: BattleRoom,
    currentUserId: String?,
    participants: List<BattleParticipant>,
    explorers: List<LeaderboardEntry>,
    roomInvites: List<BattleInvite>,
    actionBusy: Boolean,
    invitingUserId: String?,
    feedbackMessage: String?,
    onDismissFeedback: () -> Unit,
    onBackRequest: () -> Unit,
    onShowRules: () -> Unit,
    onInviteFriend: (LeaderboardEntry) -> Unit,
    onStartBattle: () -> Unit
) {
    val atlasColors = LocalAtlasColors.current
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var copiedMessage by remember { mutableStateOf<String?>(null) }
    val isHost = room.hostUserId == currentUserId
    val shareTemplate = stringResource(R.string.battle_share_text, room.roomCode)
    val copiedTemplate = stringResource(R.string.battle_code_copied, room.roomCode)

    Column(
        modifier = Modifier
            .widthIn(max = 620.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBackRequest,
                modifier = Modifier.testTag("battle_room_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.back_button)
                )
            }
            Spacer(Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.battle_status_waiting),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = room.roomCode,
                    style = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.Monospace),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
            Spacer(Modifier.width(4.dp))
            IconButton(
                onClick = onShowRules,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("battle_rules_info_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = stringResource(R.string.battle_rules_cd),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (feedbackMessage != null || copiedMessage != null) {
            Spacer(Modifier.height(10.dp))
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = copiedMessage ?: feedbackMessage.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = {
                            copiedMessage = null
                            onDismissFeedback()
                        }
                    ) {
                        Text(stringResource(R.string.ok_button))
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Shareable Room Code & Topics Card
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = atlasColors.cardElevated,
            border = BorderStroke(1.dp, atlasColors.subtleBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.battle_room_code_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = room.roomCode,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("battle_room_code_display")
                )

                Spacer(Modifier.height(14.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(room.roomCode))
                            copiedMessage = copiedTemplate
                        },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("battle_copy_code_button")
                    ) {
                        Icon(Icons.Rounded.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(stringResource(R.string.battle_copy_code))
                    }

                    FilledTonalButton(
                        onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareTemplate)
                            }
                            context.startActivity(Intent.createChooser(sendIntent, null))
                        },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("battle_share_code_button")
                    ) {
                        Icon(Icons.Rounded.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(stringResource(R.string.battle_share_code))
                    }
                }

                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = atlasColors.subtleBorder)
                Spacer(Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    room.topicCategories.forEach { cat ->
                        Surface(
                            color = cat.accentColor.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = stringResource(cat.titleRes),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "${stringResource(room.quizMode.labelRes)} • ${stringResource(room.quizDifficulty.labelRes)} • ${stringResource(R.string.question_count_chip, room.questionCount)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                    Surface(
                        color = atlasColors.goldContainer.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = stringResource(R.string.battle_host_fixed_notice),
                            style = MaterialTheme.typography.labelSmall,
                            color = atlasColors.onGoldContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // Challengers Roster
        Text(
            text = stringResource(R.string.battle_roster_title, participants.size.coerceAtLeast(room.memberUids.size)),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            participants.forEach { player ->
                val isPlayerHost = player.userId == room.hostUserId
                val isMe = player.userId == currentUserId
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = atlasColors.cardElevated,
                    border = BorderStroke(1.dp, atlasColors.subtleBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        AvatarEmblem(avatar = AvatarOption.fromId(player.avatarId), size = 40.dp)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = player.displayName,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "@${player.handle}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (isPlayerHost) {
                            Surface(
                                color = atlasColors.goldContainer,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = stringResource(R.string.battle_host_badge),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = atlasColors.onGoldContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        } else if (isMe) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = stringResource(R.string.battle_you_badge),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Start / Waiting Action
        Spacer(Modifier.height(18.dp))
        if (isHost) {
            Button(
                onClick = onStartBattle,
                enabled = !actionBusy,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("battle_start_button")
            ) {
                if (actionBusy) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.battle_start_button))
                }
            }
        } else {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.HourglassTop,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.battle_waiting_host_msg, room.hostDisplayName),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Host Direct Friend Invitation Panel
        if (isHost && explorers.isNotEmpty()) {
            Spacer(Modifier.height(22.dp))
            Text(
                text = stringResource(R.string.battle_invite_friends_header),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(10.dp))

            val invitesByUserId = roomInvites.associateBy { it.inviteeUserId }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                explorers.take(20).forEach { friend ->
                    val alreadyJoined = friend.userId in room.memberUids
                    val invite = invitesByUserId[friend.userId]
                    val alreadyPending = invite?.inviteStatus == BattleInviteStatus.PENDING

                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = atlasColors.cardElevated,
                        border = BorderStroke(1.dp, atlasColors.subtleBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            AvatarEmblem(avatar = AvatarOption.fromId(friend.avatarId), size = 38.dp)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = friend.displayName,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "@${friend.handle} • ${friend.totalScore} pts",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            when {
                                alreadyJoined -> {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.battle_joined_chip),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                                alreadyPending -> {
                                    Surface(
                                        color = atlasColors.goldContainer,
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.battle_invited_chip),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = atlasColors.onGoldContainer,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                                else -> {
                                    FilledTonalButton(
                                        onClick = { onInviteFriend(friend) },
                                        enabled = invitingUserId == null,
                                        shape = MaterialTheme.shapes.medium,
                                        modifier = Modifier
                                            .height(48.dp)
                                            .testTag("battle_invite_${friend.userId}")
                                    ) {
                                        if (invitingUserId == friend.userId) {
                                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                        } else {
                                            Icon(Icons.Rounded.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text(stringResource(R.string.battle_invite_button))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun BattleArenaContent(
    room: BattleRoom,
    playState: BattlePlayState,
    participants: List<BattleParticipant>,
    currentUserId: String?,
    onBackRequest: () -> Unit,
    onShowRules: () -> Unit,
    onSelectOption: (String) -> Unit,
    onGuessInputChange: (String) -> Unit,
    onRevealHint: () -> Unit,
    onSubmitGuess: () -> Unit,
    onSkipGuess: () -> Unit,
    onNext: () -> Unit
) {
    val atlasColors = LocalAtlasColors.current
    val question = playState.currentQuestion
    val roomMode = room.quizMode
    val progress by animateFloatAsState(
        targetValue = (playState.currentIndex + 1).toFloat() / playState.questions.size.toFloat(),
        label = "battle_progress"
    )
    val timerFraction by animateFloatAsState(
        targetValue = if (playState.maxSeconds > 0) {
            playState.secondsRemaining.toFloat() / playState.maxSeconds.toFloat()
        } else 1f,
        label = "battle_timer"
    )

    Column(
        modifier = Modifier
            .widthIn(max = 620.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // HUD Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBackRequest,
                modifier = Modifier.testTag("battle_arena_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.leave_button)
                )
            }
            Spacer(Modifier.width(4.dp))
            Text(
                text = "${playState.currentIndex + 1} / ${playState.questions.size}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.weight(1f))

            if (playState.currentStreak > 0) {
                Surface(
                    color = atlasColors.goldContainer,
                    shape = RoundedCornerShape(50)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocalFireDepartment,
                            contentDescription = null,
                            tint = atlasColors.gold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "${playState.currentStreak}x",
                            style = MaterialTheme.typography.labelMedium,
                            color = atlasColors.onGoldContainer
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
            }

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = stringResource(R.string.battle_xp_badge, playState.score),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
            Spacer(Modifier.width(4.dp))
            IconButton(
                onClick = onShowRules,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("battle_rules_info_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = stringResource(R.string.battle_rules_cd),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Live Multiplayer Standings Strip
        if (participants.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(participants, key = { it.userId }) { player ->
                    val isMe = player.userId == currentUserId
                    Surface(
                        color = if (isMe) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        } else {
                            atlasColors.cardElevated
                        },
                        border = BorderStroke(1.dp, atlasColors.subtleBorder),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            AvatarEmblem(avatar = AvatarOption.fromId(player.avatarId), size = 24.dp)
                            Spacer(Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = player.displayName,
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${stringResource(R.string.battle_xp_badge, player.score)} • ${
                                        if (player.finished) {
                                            stringResource(R.string.battle_finished_badge)
                                        } else {
                                            stringResource(R.string.battle_progress_chip, player.answeredCount, playState.questions.size)
                                        }
                                    }",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        LinearProgressIndicator(
            progress = { progress },
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50))
        )

        if (playState.timerEnabled) {
            Spacer(Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Rounded.Timer,
                    contentDescription = null,
                    tint = if (playState.secondsRemaining <= 5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                LinearProgressIndicator(
                    progress = { timerFraction },
                    color = if (playState.secondsRemaining <= 5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${playState.secondsRemaining}s",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (playState.secondsRemaining <= 5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryIllustrationBadge(category = question.category, size = 30.dp)
            Text(
                text = stringResource(question.category.titleRes),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = stringResource(question.difficulty.labelRes),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        if (question.pictureQuestion) {
            PictureQuizMediaFrame(question = question)
            Spacer(Modifier.height(16.dp))
        }

        Surface(
            shape = MaterialTheme.shapes.large,
            color = atlasColors.cardElevated,
            border = BorderStroke(1.dp, atlasColors.subtleBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = question.questionText,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(20.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        if (roomMode == QuizMode.GUESS) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = atlasColors.cardElevated,
                border = BorderStroke(1.dp, atlasColors.subtleBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = playState.guessInput,
                        onValueChange = onGuessInputChange,
                        enabled = !playState.revealed,
                        singleLine = true,
                        label = { Text(stringResource(R.string.type_your_answer)) },
                        placeholder = { Text(stringResource(R.string.enter_best_guess)) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (!playState.revealed && playState.guessInput.isNotBlank()) {
                                    onSubmitGuess()
                                }
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("battle_guess_input")
                    )

                    if (playState.hintRevealed) {
                        Spacer(Modifier.height(10.dp))
                        Surface(
                            color = atlasColors.goldContainer,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = playState.hintPattern,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = atlasColors.onGoldContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }

                    if (!playState.revealed) {
                        Spacer(Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = onRevealHint,
                                enabled = !playState.hintRevealed,
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Icon(Icons.Rounded.Lightbulb, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(stringResource(if (playState.hintRevealed) R.string.hint_shown else R.string.hint_button))
                            }
                            Button(
                                onClick = onSubmitGuess,
                                enabled = playState.guessInput.isNotBlank(),
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .weight(1.4f)
                                    .height(48.dp)
                                    .testTag("battle_submit_guess_button")
                            ) {
                                Text(stringResource(R.string.submit_guess))
                            }
                            TextButton(
                                onClick = onSkipGuess,
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text(stringResource(R.string.pass_button))
                            }
                        }
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                question.allOptions.forEachIndexed { index, option ->
                    val isSelected = playState.selectedOption == option
                    val isCorrectOption = option == question.correctAnswer

                    val targetBg = when {
                        !playState.revealed -> atlasColors.cardElevated
                        isCorrectOption -> atlasColors.correct.copy(alpha = 0.16f)
                        isSelected -> atlasColors.incorrect.copy(alpha = 0.16f)
                        else -> atlasColors.cardElevated.copy(alpha = 0.55f)
                    }
                    val targetBorder = when {
                        !playState.revealed -> atlasColors.subtleBorder
                        isCorrectOption -> atlasColors.correct
                        isSelected -> atlasColors.incorrect
                        else -> atlasColors.subtleBorder.copy(alpha = 0.5f)
                    }
                    val bgColor by animateColorAsState(targetBg, label = "battle_opt_bg_$index")
                    val borderColor by animateColorAsState(targetBorder, label = "battle_opt_border_$index")

                    Surface(
                        onClick = { if (!playState.revealed) onSelectOption(option) },
                        enabled = !playState.revealed,
                        shape = MaterialTheme.shapes.medium,
                        color = bgColor,
                        border = BorderStroke(
                            if (playState.revealed && (isCorrectOption || isSelected)) 1.5.dp else 1.dp,
                            borderColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("battle_option_$index")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = ('A' + index).toString(),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            if (playState.revealed && isCorrectOption) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = atlasColors.correct,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else if (playState.revealed && isSelected && !isCorrectOption) {
                                Icon(
                                    imageVector = Icons.Rounded.Cancel,
                                    contentDescription = null,
                                    tint = atlasColors.incorrect,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(visible = playState.revealed) {
            Column(modifier = Modifier.padding(top = 18.dp)) {
                val lastReview = playState.reviewItems.lastOrNull()
                val isNoAnswer = !playState.lastAnswerCorrect && (playState.timedOut || lastReview?.userAnswer.isNullOrBlank())
                val feedbackBg = when {
                    playState.lastAnswerCorrect -> atlasColors.correct.copy(alpha = 0.12f)
                    isNoAnswer -> atlasColors.goldContainer.copy(alpha = 0.55f)
                    else -> atlasColors.incorrect.copy(alpha = 0.12f)
                }
                val feedbackFg = when {
                    playState.lastAnswerCorrect -> atlasColors.correct
                    isNoAnswer -> atlasColors.onGoldContainer
                    else -> atlasColors.incorrect
                }
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = feedbackBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = when {
                                playState.lastAnswerCorrect -> "+${lastReview?.pointsEarned ?: 0L} XP"
                                playState.timedOut -> stringResource(R.string.battle_no_answer_zero_xp)
                                isNoAnswer -> stringResource(R.string.battle_pass_zero_xp)
                                else -> stringResource(
                                    R.string.battle_wrong_minus_xp,
                                    lastReview?.pointsEarned ?: 0L
                                )
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = feedbackFg
                        )
                        if (!playState.lastAnswerCorrect) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.answer_label, question.correctAnswer),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (!lastReview?.explanation.isNullOrBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = lastReview!!.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                Button(
                    onClick = onNext,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("battle_next_button")
                ) {
                    Text(
                        stringResource(
                            if (playState.isLastQuestion) R.string.finish_view_explanations else R.string.next_question
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun BattlePodiumContent(
    room: BattleRoom,
    playState: BattlePlayState,
    participants: List<BattleParticipant>,
    currentUserId: String?,
    actionBusy: Boolean,
    onShowRules: () -> Unit,
    onFinishRoomNow: () -> Unit,
    onBackToArena: () -> Unit
) {
    val atlasColors = LocalAtlasColors.current
    val remainingCount = (room.memberUids.size - participants.count { it.finished }).coerceAtLeast(0)
    var showMissedOnly by remember { mutableStateOf(false) }
    val hasUserResponses = playState.reviewItems.any { !it.userAnswer.isNullOrBlank() || it.wasCorrect }
    val missedCount = playState.reviewItems.count { !it.wasCorrect }
    val displayedReviews = if (showMissedOnly && hasUserResponses) {
        playState.reviewItems.filter { !it.wasCorrect }
    } else {
        playState.reviewItems
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .widthIn(max = 620.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onShowRules,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("battle_rules_info_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = stringResource(R.string.battle_rules_cd),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Surface(
            color = atlasColors.goldContainer,
            shape = CircleShape,
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.EmojiEvents,
                    contentDescription = null,
                    tint = atlasColors.gold,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.battle_podium_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            text = "${room.roomCode} • ${stringResource(room.quizMode.labelRes)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(14.dp))

        Surface(
            color = if (remainingCount == 0 || room.battleStatus == BattleStatus.FINISHED) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                atlasColors.goldContainer
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (remainingCount == 0 || room.battleStatus == BattleStatus.FINISHED) {
                    stringResource(R.string.battle_all_finished)
                } else {
                    stringResource(R.string.battle_waiting_others, remainingCount)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (remainingCount == 0 || room.battleStatus == BattleStatus.FINISHED) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    atlasColors.onGoldContainer
                },
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(14.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Live Podium Standings List
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            participants.forEachIndexed { index, player ->
                val rank = index + 1
                val isMe = player.userId == currentUserId
                val badgeBg = when (rank) {
                    1 -> atlasColors.goldContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
                val badgeFg = when (rank) {
                    1 -> atlasColors.onGoldContainer
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = if (isMe) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                    } else {
                        atlasColors.cardElevated
                    },
                    border = BorderStroke(if (rank == 1) 1.5.dp else 1.dp, if (rank == 1) atlasColors.gold else atlasColors.subtleBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("battle_podium_rank_$rank")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Surface(
                            color = badgeBg,
                            shape = CircleShape,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "#$rank",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = badgeFg
                                )
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        AvatarEmblem(avatar = AvatarOption.fromId(player.avatarId), size = 40.dp)
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isMe) "${player.displayName} (${stringResource(R.string.battle_you_badge)})" else player.displayName,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${player.correctCount}/${playState.questions.size} • ${player.durationSeconds}s • ${
                                    if (player.finished) {
                                        stringResource(R.string.battle_finished_badge)
                                    } else {
                                        stringResource(R.string.battle_progress_chip, player.answeredCount, playState.questions.size)
                                    }
                                }",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = stringResource(R.string.battle_xp_badge, player.score),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (room.battleStatus == BattleStatus.ACTIVE && room.hostUserId == currentUserId) {
                OutlinedButton(
                    onClick = onFinishRoomNow,
                    enabled = !actionBusy,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text(stringResource(R.string.battle_lock_results_button))
                }
            }
            Button(
                onClick = onBackToArena,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("battle_podium_done_button")
            ) {
                Text(stringResource(R.string.battle_open_arena))
            }
        }

        Spacer(Modifier.height(24.dp))

        // Shared Question Explanations
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoStories,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.question_explanations_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
        }

        if (hasUserResponses) {
            Spacer(Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = !showMissedOnly,
                    onClick = { showMissedOnly = false },
                    label = { Text(stringResource(R.string.filter_all_count, playState.reviewItems.size)) }
                )
                FilterChip(
                    selected = showMissedOnly,
                    onClick = { showMissedOnly = true },
                    label = { Text(stringResource(R.string.filter_missed_count, missedCount)) }
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            displayedReviews.forEach { item ->
                BattleQuestionReviewCard(review = item, showStatus = hasUserResponses)
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun BattleQuestionReviewCard(
    review: QuestionReviewItem,
    showStatus: Boolean
) {
    val atlasColors = LocalAtlasColors.current
    val isNoAnswer = !review.wasCorrect && (review.timedOut || review.userAnswer.isNullOrBlank())
    val statusColor = when {
        review.wasCorrect -> atlasColors.correct
        isNoAnswer -> atlasColors.gold
        else -> atlasColors.incorrect
    }

    Surface(
        shape = MaterialTheme.shapes.large,
        color = atlasColors.cardElevated,
        border = BorderStroke(1.dp, atlasColors.subtleBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "Q${review.index}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = stringResource(review.question.category.titleRes),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (showStatus) {
                    Surface(
                        color = statusColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = when {
                                    review.wasCorrect -> Icons.Rounded.CheckCircle
                                    isNoAnswer -> Icons.Rounded.HourglassTop
                                    else -> Icons.Rounded.Cancel
                                },
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = when {
                                    review.wasCorrect -> "+${review.pointsEarned} XP"
                                    isNoAnswer -> stringResource(R.string.battle_status_no_answer)
                                    else -> "${review.pointsEarned} XP"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = statusColor
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            if (review.question.pictureQuestion) {
                PictureQuizMediaFrame(
                    question = review.question,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            Text(
                text = review.question.questionText,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(8.dp))

            if (showStatus && !review.wasCorrect && !review.userAnswer.isNullOrBlank()) {
                Text(
                    text = stringResource(R.string.your_answer_label, review.userAnswer),
                    style = MaterialTheme.typography.bodySmall,
                    color = atlasColors.incorrect
                )
            }

            Text(
                text = stringResource(R.string.correct_answer_label, review.question.correctAnswer),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = atlasColors.correct
            )

            Spacer(Modifier.height(10.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = stringResource(R.string.explanation_caps),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = review.explanation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
internal fun BattleRulesInfoDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val initialCode = remember(context) { LanguageStorage.effectiveLanguageCode(context) }
    var selectedLangCode by remember { mutableStateOf(initialCode) }

    val localizedResources = remember(selectedLangCode, context) {
        val locale = when (selectedLangCode) {
            "es" -> Locale("es")
            "in", "id" -> Locale("in", "ID")
            else -> Locale("en")
        }
        val config = Configuration(context.resources.configuration).apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
        context.createConfigurationContext(config).resources
    }

    val ruleResIds = remember {
        listOf(
            R.string.battle_rule_1,
            R.string.battle_rule_2,
            R.string.battle_rule_3,
            R.string.battle_rule_4,
            R.string.battle_rule_5,
            R.string.battle_rule_6
        )
    }

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
            Text(
                text = localizedResources.getString(R.string.battle_rules_title),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(
                        AppLanguage.ENGLISH,
                        AppLanguage.SPANISH,
                        AppLanguage.INDONESIAN
                    ).forEach { lang ->
                        FilterChip(
                            selected = selectedLangCode == lang.code,
                            onClick = { selectedLangCode = lang.code },
                            label = {
                                Text(
                                    text = lang.shortBadge,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            modifier = Modifier.testTag("battle_rules_lang_${lang.code}")
                        )
                    }
                }

                HorizontalDivider()

                ruleResIds.forEach { resId ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = localizedResources.getString(resId),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("battle_rules_dismiss_button")
            ) {
                Text(localizedResources.getString(R.string.battle_rules_got_it))
            }
        }
    )
}
