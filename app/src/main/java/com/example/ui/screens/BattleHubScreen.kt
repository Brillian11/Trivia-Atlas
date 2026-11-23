package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.AvatarOption
import com.example.data.model.BattleInvite
import com.example.data.model.BattleRoom
import com.example.data.model.BattleStatus
import com.example.data.model.LeaderboardEntry
import com.example.data.model.QuizDifficulty
import com.example.data.model.QuizMode
import com.example.data.model.TriviaCategory
import com.example.data.model.WikipediaArticle
import com.example.ui.auth.GoogleSignInButton
import com.example.ui.components.AvatarEmblem
import com.example.ui.theme.LocalAtlasColors
import com.example.ui.viewmodel.BattleHubViewModel
import com.example.ui.viewmodel.UiState

@Composable
fun BattleHubScreen(
    viewModel: BattleHubViewModel,
    onOpenRoom: (String) -> Unit,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val incomingInvites by viewModel.incomingInvites.collectAsStateWithLifecycle()
    val myRoomsState by viewModel.myRooms.collectAsStateWithLifecycle()
    val explorers by viewModel.explorers.collectAsStateWithLifecycle()
    val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()
    val busyInviteId by viewModel.busyInviteId.collectAsStateWithLifecycle()
    val errorCode by viewModel.errorCode.collectAsStateWithLifecycle()
    val wikiSearchQuery by viewModel.wikiSearchQuery.collectAsStateWithLifecycle()
    val wikiSearchState by viewModel.wikiSearchState.collectAsStateWithLifecycle()
    val selectedBattleArticle by viewModel.selectedBattleArticle.collectAsStateWithLifecycle()
    val customUrlLoading by viewModel.customUrlLoading.collectAsStateWithLifecycle()
    val customUrlError by viewModel.customUrlError.collectAsStateWithLifecycle()
    val atlasColors = LocalAtlasColors.current

    var roomCodeInput by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showRulesDialog by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }

    if (showRulesDialog) {
        BattleRulesInfoDialog(onDismiss = { showRulesDialog = false })
    }

    if (showCreateDialog && !viewModel.isGuest) {
        CreateBattleRoomDialog(
            explorers = explorers,
            isSubmitting = isSubmitting,
            wikiSearchQuery = wikiSearchQuery,
            wikiSearchState = wikiSearchState,
            selectedArticle = selectedBattleArticle,
            customUrlLoading = customUrlLoading,
            customUrlError = customUrlError,
            creationErrorCode = errorCode,
            onWikiSearchQueryChanged = viewModel::onWikiSearchQueryChanged,
            onSubmitWikiSearch = viewModel::submitWikipediaSearch,
            onClearWikiSearch = viewModel::clearWikiSearch,
            onSelectArticle = viewModel::selectBattleArticle,
            onClearSelectedArticle = viewModel::clearSelectedBattleArticle,
            onLoadCustomUrl = viewModel::loadCustomBattleArticleUrl,
            onClearCustomUrlError = viewModel::clearCustomUrlError,
            onDismiss = {
                if (!isSubmitting && !customUrlLoading) {
                    showCreateDialog = false
                    viewModel.resetArticleSelection()
                }
            },
            onCreate = { topics, difficulty, mode, count, invitees, chosenArticle, customUrlInput ->
                viewModel.createRoom(
                    topics = topics,
                    difficulty = difficulty,
                    mode = mode,
                    questionCount = count,
                    selectedInvitees = invitees,
                    selectedArticle = chosenArticle,
                    customUrlToLoad = customUrlInput,
                    onCreated = { createdCode ->
                        showCreateDialog = false
                        onOpenRoom(createdCode)
                    }
                )
            }
        )
    }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier.fillMaxSize()
    ) {
        if (viewModel.isGuest) {
            Column(
                modifier = Modifier
                    .widthIn(max = 620.dp)
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.battle_hub_title),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.battle_hub_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { showRulesDialog = true },
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

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = atlasColors.cardElevated,
                        border = BorderStroke(1.dp, atlasColors.subtleBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("battle_guest_signin_card")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Groups,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(Modifier.height(14.dp))
                            Text(
                                text = stringResource(R.string.battle_guest_title),
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.battle_guest_body),
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
            }
            return@Box
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .widthIn(max = 620.dp)
                .fillMaxSize()
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.battle_hub_title),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.battle_hub_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { showRulesDialog = true },
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
            }

            if (errorCode != null) {
                item {
                    val resolvedError = when (errorCode) {
                        "ROOM_CODE_INVALID" -> stringResource(R.string.battle_error_code_invalid)
                        "ROOM_NOT_FOUND" -> stringResource(R.string.battle_error_room_not_found)
                        "ROOM_ALREADY_STARTED" -> stringResource(R.string.battle_error_already_started)
                        "ROOM_FULL" -> stringResource(R.string.battle_error_room_full)
                        "ARTICLE_QUIZ_FAILED" -> stringResource(R.string.battle_error_article_quiz)
                        "BATTLE_ACTION_FAILED" -> stringResource(R.string.battle_error_generic)
                        else -> errorCode!!
                    }
                    AnimatedVisibility(visible = true) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = resolvedError,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = viewModel::clearError,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = stringResource(R.string.close_button),
                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Host New Battle Room Card
            item {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = atlasColors.cardElevated,
                    border = BorderStroke(1.dp, atlasColors.subtleBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.SportsEsports,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.battle_home_banner_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = stringResource(R.string.battle_home_banner_sub),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        Button(
                            onClick = {
                                viewModel.clearError()
                                showCreateDialog = true
                            },
                            enabled = !isSubmitting,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("battle_host_button")
                        ) {
                            Icon(Icons.Rounded.AddCircleOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.battle_create_room_cta))
                        }
                    }
                }
            }

            // Join via Room Code Card
            item {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = atlasColors.cardElevated,
                    border = BorderStroke(1.dp, atlasColors.subtleBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = stringResource(R.string.battle_join_code_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = roomCodeInput,
                                onValueChange = { next ->
                                    roomCodeInput = next.uppercase().filter { it.isLetterOrDigit() }.take(12)
                                },
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.battle_room_code_hint),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Characters,
                                    imeAction = ImeAction.Go
                                ),
                                keyboardActions = KeyboardActions(
                                    onGo = {
                                        if (roomCodeInput.length >= 4 && !isSubmitting) {
                                            viewModel.joinRoomByCode(roomCodeInput, onOpenRoom)
                                        }
                                    }
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("battle_code_input")
                            )

                            FilledTonalButton(
                                onClick = { viewModel.joinRoomByCode(roomCodeInput, onOpenRoom) },
                                enabled = roomCodeInput.length >= 4 && !isSubmitting,
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .height(52.dp)
                                    .testTag("battle_join_code_button")
                            ) {
                                if (isSubmitting) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Rounded.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(stringResource(R.string.battle_join_button))
                                }
                            }
                        }
                    }
                }
            }

            // Incoming Direct Invitations
            if (incomingInvites.isNotEmpty()) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MailOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.battle_incoming_invites_title, incomingInvites.size),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                items(incomingInvites, key = { it.id }) { invite ->
                    IncomingInviteCard(
                        invite = invite,
                        isBusy = busyInviteId == invite.id,
                        onAccept = { viewModel.respondToInvite(invite, accept = true, onJoined = onOpenRoom) },
                        onDecline = { viewModel.respondToInvite(invite, accept = false, onJoined = onOpenRoom) }
                    )
                }
            }

            // Active / Recent Battle Rooms
            item {
                Text(
                    text = stringResource(R.string.battle_my_rooms_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            when (val roomsState = myRoomsState) {
                is UiState.Loading -> {
                    item {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 28.dp)
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is UiState.Error -> {
                    item {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = roomsState.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                is UiState.Success -> {
                    if (roomsState.data.isEmpty()) {
                        item {
                            Surface(
                                shape = MaterialTheme.shapes.large,
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Groups,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    Text(
                                        text = stringResource(R.string.battle_no_rooms_title),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = stringResource(R.string.battle_no_rooms_body),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        items(roomsState.data, key = { it.roomCode }) { room ->
                            BattleRoomSummaryCard(
                                room = room,
                                onClick = { onOpenRoom(room.roomCode) }
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IncomingInviteCard(
    invite: BattleInvite,
    isBusy: Boolean,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    val atlasColors = LocalAtlasColors.current
    val avatar = AvatarOption.fromId(invite.hostAvatarId)

    Surface(
        shape = MaterialTheme.shapes.large,
        color = atlasColors.goldContainer.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, atlasColors.gold.copy(alpha = 0.45f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("battle_invite_card_${invite.roomCode}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AvatarEmblem(avatar = avatar, size = 40.dp)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.battle_invite_from, invite.hostDisplayName, invite.roomCode),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${stringResource(invite.quizMode.labelRes)} • ${stringResource(R.string.question_count_chip, invite.questionCount)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (invite.topicCategories.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    invite.topicCategories.take(4).forEach { cat ->
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = stringResource(cat.titleRes),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onDecline,
                    enabled = !isBusy,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("battle_decline_${invite.roomCode}")
                ) {
                    Text(stringResource(R.string.battle_decline_button))
                }
                Button(
                    onClick = onAccept,
                    enabled = !isBusy,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("battle_accept_${invite.roomCode}")
                ) {
                    if (isBusy) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(stringResource(R.string.battle_accept_button))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BattleRoomSummaryCard(
    room: BattleRoom,
    onClick: () -> Unit
) {
    val atlasColors = LocalAtlasColors.current
    val (statusLabelRes, statusBg, statusFg) = when (room.battleStatus) {
        BattleStatus.WAITING -> Triple(
            R.string.battle_status_waiting,
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        BattleStatus.ACTIVE -> Triple(
            R.string.battle_status_active,
            atlasColors.goldContainer,
            atlasColors.onGoldContainer
        )
        BattleStatus.FINISHED -> Triple(
            R.string.battle_status_finished,
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = atlasColors.cardElevated,
        border = BorderStroke(1.dp, atlasColors.subtleBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("battle_room_card_${room.roomCode}")
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
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = room.roomCode,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = room.hostDisplayName,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = stringResource(statusLabelRes),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusFg,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                room.topicCategories.take(4).forEach { cat ->
                    Surface(
                        color = cat.accentColor.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = stringResource(cat.titleRes),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.battle_topics_count, room.topics.size, room.questionCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.battle_players_count, room.memberUids.size),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private enum class BattleQuestionSource {
    TOPICS,
    WIKIPEDIA_SEARCH,
    CUSTOM_URL
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CreateBattleRoomDialog(
    explorers: List<LeaderboardEntry>,
    isSubmitting: Boolean,
    wikiSearchQuery: String,
    wikiSearchState: UiState<List<WikipediaArticle>>?,
    selectedArticle: WikipediaArticle?,
    customUrlLoading: Boolean,
    customUrlError: String?,
    creationErrorCode: String?,
    onWikiSearchQueryChanged: (String) -> Unit,
    onSubmitWikiSearch: () -> Unit,
    onClearWikiSearch: () -> Unit,
    onSelectArticle: (WikipediaArticle) -> Unit,
    onClearSelectedArticle: () -> Unit,
    onLoadCustomUrl: (String) -> Unit,
    onClearCustomUrlError: () -> Unit,
    onDismiss: () -> Unit,
    onCreate: (
        topics: Set<TriviaCategory>,
        difficulty: QuizDifficulty,
        mode: QuizMode,
        count: Int,
        invitees: List<LeaderboardEntry>,
        selectedArticle: WikipediaArticle?,
        customUrlInput: String?
    ) -> Unit
) {
    val selectedTopics = remember { mutableStateListOf(TriviaCategory.GENERAL_KNOWLEDGE, TriviaCategory.SCIENCE) }
    var selectedMode by remember { mutableStateOf(QuizMode.STANDARD) }
    var selectedSource by remember { mutableStateOf(BattleQuestionSource.TOPICS) }
    var customUrlInput by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf(QuizDifficulty.ALL) }
    var selectedCount by remember { mutableIntStateOf(10) }
    val selectedInvitees = remember { mutableStateListOf<String>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.battle_setup_dialog_title),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (creationErrorCode == "ARTICLE_QUIZ_FAILED") {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.battle_error_article_quiz),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // Mode selection
                Text(
                    text = stringResource(R.string.category_play_mode),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    QuizMode.entries.forEach { mode ->
                        FilterChip(
                            selected = selectedMode == mode,
                            onClick = { selectedMode = mode },
                            label = { Text(stringResource(mode.labelRes)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.testTag("battle_mode_${mode.key}")
                        )
                    }
                }

                // Question Source Selector (Topics vs Search Wikipedia vs Custom URL)
                if (selectedMode != QuizMode.PICTURE) {
                    Text(
                        text = stringResource(R.string.battle_source_label),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedSource == BattleQuestionSource.TOPICS,
                            onClick = { selectedSource = BattleQuestionSource.TOPICS },
                            label = { Text(stringResource(R.string.battle_source_topics)) },
                            modifier = Modifier.testTag("battle_source_topics")
                        )
                        FilterChip(
                            selected = selectedSource == BattleQuestionSource.WIKIPEDIA_SEARCH,
                            onClick = { selectedSource = BattleQuestionSource.WIKIPEDIA_SEARCH },
                            leadingIcon = {
                                Icon(Icons.Rounded.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            label = { Text(stringResource(R.string.battle_source_wikipedia)) },
                            modifier = Modifier.testTag("battle_source_wikipedia")
                        )
                        FilterChip(
                            selected = selectedSource == BattleQuestionSource.CUSTOM_URL,
                            onClick = { selectedSource = BattleQuestionSource.CUSTOM_URL },
                            leadingIcon = {
                                Icon(Icons.Rounded.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            label = { Text(stringResource(R.string.battle_source_custom_url)) },
                            modifier = Modifier.testTag("battle_source_custom_url")
                        )
                    }

                    when (selectedSource) {
                        BattleQuestionSource.TOPICS -> {
                            Text(
                                text = stringResource(R.string.battle_pick_topics_label),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                TriviaCategory.playableGridCategories.forEach { cat ->
                                    val isSelected = cat in selectedTopics
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            if (isSelected) {
                                                if (selectedTopics.size > 1) {
                                                    selectedTopics.remove(cat)
                                                }
                                            } else {
                                                selectedTopics.add(cat)
                                            }
                                        },
                                        label = { Text(stringResource(cat.titleRes)) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        modifier = Modifier.testTag("battle_topic_${cat.apiKey}")
                                    )
                                }
                            }
                        }

                        BattleQuestionSource.WIKIPEDIA_SEARCH -> {
                            if (selectedArticle != null) {
                                SelectedBattleArticleCard(
                                    article = selectedArticle,
                                    onClear = onClearSelectedArticle
                                )
                            } else {
                                OutlinedTextField(
                                    value = wikiSearchQuery,
                                    onValueChange = onWikiSearchQueryChanged,
                                    placeholder = {
                                        Text(
                                            text = stringResource(R.string.battle_wiki_search_hint),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    singleLine = true,
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Rounded.Search,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    trailingIcon = {
                                        if (wikiSearchQuery.isNotEmpty()) {
                                            IconButton(
                                                onClick = onClearWikiSearch,
                                                modifier = Modifier.testTag("battle_wiki_search_clear")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Close,
                                                    contentDescription = stringResource(R.string.wiki_search_clear_cd)
                                                )
                                            }
                                        }
                                    },
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                    keyboardActions = KeyboardActions(onSearch = { onSubmitWikiSearch() }),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("battle_wiki_search_input")
                                )

                                if (wikiSearchQuery.trim().length >= 2 && wikiSearchState != null) {
                                    when (wikiSearchState) {
                                        is UiState.Loading -> {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier.padding(vertical = 4.dp)
                                            ) {
                                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                                Text(
                                                    text = stringResource(R.string.wiki_searching),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        is UiState.Error -> {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = if (wikiSearchState.message == "OFFLINE") {
                                                        stringResource(R.string.wiki_offline_msg)
                                                    } else {
                                                        stringResource(R.string.wiki_search_error)
                                                    },
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                TextButton(onClick = onSubmitWikiSearch) {
                                                    Text(stringResource(R.string.retry_button))
                                                }
                                            }
                                        }

                                        is UiState.Success -> {
                                            val results = wikiSearchState.data
                                            if (results.isEmpty()) {
                                                Text(
                                                    text = stringResource(R.string.wiki_search_empty, wikiSearchQuery.trim()),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            } else {
                                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    results.take(5).forEachIndexed { idx, article ->
                                                        Surface(
                                                            onClick = { onSelectArticle(article) },
                                                            shape = MaterialTheme.shapes.medium,
                                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .testTag("battle_wiki_result_$idx")
                                                        ) {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Rounded.Public,
                                                                    contentDescription = null,
                                                                    tint = MaterialTheme.colorScheme.primary,
                                                                    modifier = Modifier.size(18.dp)
                                                                )
                                                                Column(modifier = Modifier.weight(1f)) {
                                                                    Text(
                                                                        text = article.title,
                                                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
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
                                                                            maxLines = 1,
                                                                            overflow = TextOverflow.Ellipsis
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            Text(
                                text = stringResource(R.string.battle_article_quiz_note),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        BattleQuestionSource.CUSTOM_URL -> {
                            if (selectedArticle != null) {
                                SelectedBattleArticleCard(
                                    article = selectedArticle,
                                    onClear = {
                                        onClearSelectedArticle()
                                        onClearCustomUrlError()
                                    }
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = customUrlInput,
                                        onValueChange = {
                                            customUrlInput = it
                                            if (customUrlError != null) onClearCustomUrlError()
                                        },
                                        label = { Text(stringResource(R.string.wiki_custom_url_input_label)) },
                                        placeholder = { Text("https://en.wikipedia.org/wiki/...") },
                                        singleLine = true,
                                        enabled = !customUrlLoading && !isSubmitting,
                                        isError = customUrlError != null,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Uri,
                                            imeAction = ImeAction.Go,
                                            autoCorrectEnabled = false
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onGo = {
                                                if (customUrlInput.isNotBlank() && !customUrlLoading) {
                                                    onLoadCustomUrl(customUrlInput)
                                                }
                                            }
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("battle_custom_url_input")
                                    )
                                    FilledTonalButton(
                                        onClick = { onLoadCustomUrl(customUrlInput) },
                                        enabled = customUrlInput.isNotBlank() && !customUrlLoading && !isSubmitting,
                                        shape = MaterialTheme.shapes.medium,
                                        modifier = Modifier
                                            .height(48.dp)
                                            .testTag("battle_load_url_button")
                                    ) {
                                        if (customUrlLoading) {
                                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                        } else {
                                            Text(stringResource(R.string.battle_load_url_button))
                                        }
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
                                        modifier = Modifier.testTag("battle_custom_url_error")
                                    )
                                }
                            }
                            Text(
                                text = stringResource(R.string.battle_article_quiz_note),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Difficulty
                Text(
                    text = stringResource(R.string.difficulty_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    QuizDifficulty.entries.forEach { diff ->
                        FilterChip(
                            selected = selectedDifficulty == diff,
                            onClick = { selectedDifficulty = diff },
                            label = { Text(stringResource(diff.labelRes)) }
                        )
                    }
                }

                // Question count
                Text(
                    text = stringResource(R.string.battle_questions_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(5, 10, 15).forEach { count ->
                        FilterChip(
                            selected = selectedCount == count,
                            onClick = { selectedCount = count },
                            label = { Text(stringResource(R.string.question_count_chip, count)) },
                            modifier = Modifier.testTag("battle_count_$count")
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.battle_host_fixed_notice),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider()

                // Optional Direct Friend Invites
                Text(
                    text = stringResource(R.string.battle_invite_friends_optional),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                if (explorers.isEmpty()) {
                    Text(
                        text = stringResource(R.string.battle_no_friends_available),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        explorers.take(15).forEach { explorer ->
                            val checked = explorer.userId in selectedInvitees
                            Surface(
                                shape = MaterialTheme.shapes.medium,
                                color = if (checked) {
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (checked) {
                                            selectedInvitees.remove(explorer.userId)
                                        } else if (selectedInvitees.size < 11) {
                                            selectedInvitees.add(explorer.userId)
                                        }
                                    }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    AvatarEmblem(
                                        avatar = AvatarOption.fromId(explorer.avatarId),
                                        size = 32.dp
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = explorer.displayName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "@${explorer.handle}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Checkbox(
                                        checked = checked,
                                        onCheckedChange = { isNowChecked ->
                                            if (isNowChecked && selectedInvitees.size < 11 && !checked) {
                                                selectedInvitees.add(explorer.userId)
                                            } else if (!isNowChecked) {
                                                selectedInvitees.remove(explorer.userId)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            val canConfirm = when {
                isSubmitting || customUrlLoading -> false
                selectedMode == QuizMode.PICTURE -> true
                selectedSource == BattleQuestionSource.TOPICS -> true
                selectedSource == BattleQuestionSource.WIKIPEDIA_SEARCH -> selectedArticle != null
                selectedSource == BattleQuestionSource.CUSTOM_URL -> selectedArticle != null || customUrlInput.isNotBlank()
                else -> true
            }
            Button(
                onClick = {
                    val chosenFriends = explorers.filter { it.userId in selectedInvitees }
                    val articleToUse = if (selectedMode != QuizMode.PICTURE && selectedSource != BattleQuestionSource.TOPICS) {
                        selectedArticle
                    } else {
                        null
                    }
                    val urlToUse = if (selectedMode != QuizMode.PICTURE && selectedSource == BattleQuestionSource.CUSTOM_URL && articleToUse == null) {
                        customUrlInput.trim()
                    } else {
                        null
                    }
                    onCreate(
                        selectedTopics.toSet(),
                        selectedDifficulty,
                        selectedMode,
                        selectedCount,
                        chosenFriends,
                        articleToUse,
                        urlToUse
                    )
                },
                enabled = canConfirm,
                modifier = Modifier.testTag("battle_confirm_create_button")
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.battle_creating_room))
                } else {
                    Text(stringResource(R.string.battle_create_and_open))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSubmitting && !customUrlLoading) {
                Text(stringResource(R.string.cancel_button))
            }
        }
    )
}

@Composable
private fun SelectedBattleArticleCard(
    article: WikipediaArticle,
    onClear: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("battle_selected_article_card")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Public,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.battle_selected_article_badge),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
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
            IconButton(
                onClick = onClear,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("battle_clear_selected_article")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = stringResource(R.string.wiki_search_clear_cd)
                )
            }
        }
    }
}
