package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.QuestionReviewItem
import com.example.data.model.QuizMode
import com.example.ui.components.CategoryIllustrationBadge
import com.example.ui.components.PictureQuizMediaFrame
import com.example.ui.theme.LocalAtlasColors
import com.example.ui.viewmodel.QuizScreenState
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onExit: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showQuitDialog by remember { mutableStateOf(false) }

    val activeState = state as? QuizScreenState.Active
    BackHandler(enabled = activeState != null) {
        showQuitDialog = true
    }

    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = { showQuitDialog = false },
            title = { Text(stringResource(R.string.leave_expedition_title)) },
            text = { Text(stringResource(R.string.leave_expedition_msg)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showQuitDialog = false
                        onExit()
                    }
                ) {
                    Text(stringResource(R.string.leave_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuitDialog = false }) {
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
            when (val current = state) {
                is QuizScreenState.Loading -> {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = if (viewModel.isWikipediaQuiz) {
                                val title = viewModel.wikiArticleTitle
                                if (!title.isNullOrBlank()) {
                                    stringResource(R.string.wiki_generating_quiz, title)
                                } else {
                                    stringResource(R.string.wiki_generating_generic)
                                }
                            } else {
                                stringResource(R.string.preparing_questions)
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                is QuizScreenState.Error -> {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .widthIn(max = 460.dp)
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = when (current.message) {
                                "NO_QUESTIONS" -> stringResource(R.string.no_questions_error)
                                "OFFLINE_DISABLED" -> stringResource(R.string.offline_disabled_error)
                                "LIBRARY_EMPTY" -> stringResource(R.string.library_empty_error)
                                "LIVE_FETCH_ERROR" -> stringResource(R.string.live_fetch_error)
                                "ARTICLE_NOT_FOUND" -> stringResource(R.string.wiki_article_not_found)
                                "MISSING_CUSTOM_API_KEY" -> stringResource(R.string.wiki_missing_api_key_error)
                                "INVALID_AI_JSON" -> stringResource(R.string.wiki_ai_invalid_json)
                                "OFFLINE" -> stringResource(R.string.wiki_offline_msg)
                                else -> current.message
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(18.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(onClick = onExit) { Text(stringResource(R.string.back_button)) }
                            Button(onClick = viewModel::startRound) { Text(stringResource(R.string.retry_button)) }
                        }
                        if (viewModel.isWikipediaQuiz) {
                            Spacer(Modifier.height(10.dp))
                            FilledTonalButton(
                                onClick = viewModel::startLocalWikiFallback,
                                modifier = Modifier.testTag("wiki_error_instant_fallback_button")
                            ) {
                                Text(stringResource(R.string.wiki_ai_local_play_cta))
                            }
                        }
                    }
                }

                is QuizScreenState.Active -> {
                    ActiveQuizContent(
                        state = current,
                        onBackRequest = { showQuitDialog = true },
                        onSelectOption = viewModel::selectAnswer,
                        onGuessInputChange = viewModel::updateGuessInput,
                        onRevealHint = viewModel::revealHint,
                        onSubmitGuess = viewModel::submitGuess,
                        onSkipGuess = viewModel::skipGuess,
                        onNext = viewModel::advanceToNext
                    )
                }

                is QuizScreenState.Completed -> {
                    QuizSummaryContent(
                        completed = current,
                        onPlayAgain = viewModel::startRound,
                        onBackHome = onExit
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveQuizContent(
    state: QuizScreenState.Active,
    onBackRequest: () -> Unit,
    onSelectOption: (String) -> Unit,
    onGuessInputChange: (String) -> Unit,
    onRevealHint: () -> Unit,
    onSubmitGuess: () -> Unit,
    onSkipGuess: () -> Unit,
    onNext: () -> Unit
) {
    val atlasColors = LocalAtlasColors.current
    val question = state.currentQuestion
    val progress by animateFloatAsState(
        targetValue = (state.currentIndex + 1).toFloat() / state.questions.size.toFloat(),
        label = "quiz_progress"
    )
    val timerFraction by animateFloatAsState(
        targetValue = if (state.maxSeconds > 0) {
            state.secondsRemaining.toFloat() / state.maxSeconds.toFloat()
        } else 1f,
        label = "timer_progress"
    )

    Column(
        modifier = Modifier
            .widthIn(max = 620.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Top HUD
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBackRequest,
                modifier = Modifier.testTag("quiz_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.leave_button)
                )
            }

            Spacer(Modifier.width(4.dp))

            Text(
                text = "${state.currentIndex + 1} / ${state.questions.size}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.weight(1f))

            if (state.currentStreak > 0) {
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
                            text = "${state.currentStreak}x",
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
                    text = stringResource(R.string.pts_badge, state.score),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
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

        if (state.timerEnabled) {
            Spacer(Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Rounded.Timer,
                    contentDescription = null,
                    tint = if (state.secondsRemaining <= 5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                LinearProgressIndicator(
                    progress = { timerFraction },
                    color = if (state.secondsRemaining <= 5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${state.secondsRemaining}s",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (state.secondsRemaining <= 5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Category + Difficulty + Mode metadata row
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
            if (state.mode == QuizMode.GUESS) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = stringResource(R.string.guess_multiplier_badge),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            if (state.servedFromOfflineCache) {
                Surface(
                    color = atlasColors.goldContainer,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = stringResource(R.string.offline_badge),
                        style = MaterialTheme.typography.labelSmall,
                        color = atlasColors.onGoldContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            if (!state.wikiAiModelName.isNullOrBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.testTag("wiki_ai_model_badge")
                ) {
                    Text(
                        text = stringResource(R.string.wiki_ai_active_badge, state.wikiAiModelName),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            } else if (state.wikiAutoFallbackToInstant) {
                Surface(
                    color = atlasColors.goldContainer,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.testTag("wiki_instant_fallback_badge")
                ) {
                    Text(
                        text = stringResource(R.string.wiki_instant_fallback_badge),
                        style = MaterialTheme.typography.labelSmall,
                        color = atlasColors.onGoldContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        if (state.wikiAutoFallbackToInstant) {
            Spacer(Modifier.height(10.dp))
            Surface(
                color = atlasColors.goldContainer.copy(alpha = 0.72f),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wiki_auto_fallback_banner")
            ) {
                Text(
                    text = stringResource(R.string.wiki_ai_auto_fallback_banner),
                    style = MaterialTheme.typography.bodySmall,
                    color = atlasColors.onGoldContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        if (!state.articleTitle.isNullOrBlank()) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                shape = RoundedCornerShape(50),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Text(
                    text = state.articleTitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }
        }

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

        if (state.mode == QuizMode.GUESS) {
            GuessModeInteraction(
                state = state,
                onGuessInputChange = onGuessInputChange,
                onRevealHint = onRevealHint,
                onSubmitGuess = onSubmitGuess,
                onSkipGuess = onSkipGuess
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                question.allOptions.forEachIndexed { index, option ->
                    val isSelected = state.selectedOption == option
                    val isCorrectOption = option == question.correctAnswer

                    val targetBg = when {
                        !state.revealed -> atlasColors.cardElevated
                        isCorrectOption -> atlasColors.correct.copy(alpha = 0.16f)
                        isSelected -> atlasColors.incorrect.copy(alpha = 0.16f)
                        else -> atlasColors.cardElevated.copy(alpha = 0.55f)
                    }
                    val targetBorder = when {
                        !state.revealed -> atlasColors.subtleBorder
                        isCorrectOption -> atlasColors.correct
                        isSelected -> atlasColors.incorrect
                        else -> atlasColors.subtleBorder.copy(alpha = 0.5f)
                    }
                    val bgColor by animateColorAsState(targetBg, label = "option_bg_$index")
                    val borderColor by animateColorAsState(targetBorder, label = "option_border_$index")

                    Surface(
                        onClick = { if (!state.revealed) onSelectOption(option) },
                        enabled = !state.revealed,
                        shape = MaterialTheme.shapes.medium,
                        color = bgColor,
                        border = BorderStroke(if (state.revealed && (isCorrectOption || isSelected)) 1.5.dp else 1.dp, borderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quiz_option_$index")
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

                            if (state.revealed && isCorrectOption) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = atlasColors.correct,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else if (state.revealed && isSelected && !isCorrectOption) {
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

        AnimatedVisibility(visible = state.revealed) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                val feedbackBg = if (state.lastAnswerCorrect) {
                    atlasColors.correct.copy(alpha = 0.14f)
                } else {
                    atlasColors.incorrect.copy(alpha = 0.14f)
                }
                val feedbackFg = if (state.lastAnswerCorrect) {
                    atlasColors.correct
                } else {
                    atlasColors.incorrect
                }

                Surface(
                    color = feedbackBg,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Icon(
                            imageVector = if (state.lastAnswerCorrect) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                            contentDescription = null,
                            tint = feedbackFg,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = when {
                                    state.lastAnswerCorrect -> stringResource(
                                        R.string.correct_plus_pts,
                                        state.reviewItems.lastOrNull()?.pointsEarned ?: 0L
                                    )
                                    state.timedOut -> stringResource(R.string.times_up)
                                    else -> stringResource(R.string.not_quite)
                                },
                                style = MaterialTheme.typography.titleSmall,
                                color = feedbackFg
                            )
                            Text(
                                text = stringResource(R.string.answer_label, question.correctAnswer),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val revealExp = state.reviewItems.lastOrNull()?.explanation
                            if (!revealExp.isNullOrBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = revealExp,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = onNext,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("quiz_next_button")
                ) {
                    Text(
                        stringResource(
                            if (state.isLastQuestion) R.string.finish_view_explanations else R.string.next_question
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
private fun GuessModeInteraction(
    state: QuizScreenState.Active,
    onGuessInputChange: (String) -> Unit,
    onRevealHint: () -> Unit,
    onSubmitGuess: () -> Unit,
    onSkipGuess: () -> Unit
) {
    val atlasColors = LocalAtlasColors.current
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (state.hintRevealed) {
            Surface(
                color = atlasColors.goldContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Lightbulb,
                        contentDescription = null,
                        tint = atlasColors.gold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = state.hintPattern,
                        style = MaterialTheme.typography.labelLarge,
                        color = atlasColors.onGoldContainer
                    )
                }
            }
        }

        OutlinedTextField(
            value = state.guessInput,
            onValueChange = onGuessInputChange,
            enabled = !state.revealed,
            singleLine = true,
            label = { Text(stringResource(R.string.type_your_answer)) },
            placeholder = { Text(stringResource(R.string.enter_best_guess)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (!state.revealed && state.guessInput.isNotBlank()) {
                        onSubmitGuess()
                    }
                }
            ),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("guess_input_field")
        )

        if (!state.revealed) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                FilledTonalButton(
                    onClick = onRevealHint,
                    enabled = !state.hintRevealed,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("guess_hint_button")
                ) {
                    Icon(Icons.Rounded.Lightbulb, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(if (state.hintRevealed) R.string.hint_shown else R.string.hint_button))
                }

                Button(
                    onClick = onSubmitGuess,
                    enabled = state.guessInput.isNotBlank(),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("guess_submit_button")
                ) {
                    Text(stringResource(R.string.submit_guess))
                }

                TextButton(
                    onClick = onSkipGuess,
                    modifier = Modifier.testTag("guess_skip_button")
                ) {
                    Text(stringResource(R.string.pass_button))
                }
            }
        }
    }
}

@Composable
private fun QuizSummaryContent(
    completed: QuizScreenState.Completed,
    onPlayAgain: () -> Unit,
    onBackHome: () -> Unit
) {
    val atlasColors = LocalAtlasColors.current
    val result = completed.result
    val accuracy = if (result.totalQuestions > 0) {
        (result.correctCount * 100) / result.totalQuestions
    } else 0

    var showMissedOnly by remember { mutableStateOf(false) }
    val missedCount = completed.reviewItems.count { !it.wasCorrect }
    val displayedReviews = if (showMissedOnly) {
        completed.reviewItems.filter { !it.wasCorrect }
    } else {
        completed.reviewItems
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .widthIn(max = 620.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Spacer(Modifier.height(8.dp))

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

        Spacer(Modifier.height(14.dp))

        Text(
            text = stringResource(R.string.expedition_complete),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = if (!completed.articleTitle.isNullOrBlank()) {
                "${stringResource(result.category.titleRes)} • ${completed.articleTitle}"
            } else {
                "${stringResource(result.category.titleRes)} • ${stringResource(result.mode.labelRes)}"
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(18.dp))

        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = atlasColors.cardElevated,
            border = BorderStroke(1.dp, atlasColors.subtleBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(22.dp)
            ) {
                Text(
                    text = "${result.score}",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.points_earned_caps),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(18.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SummaryStat(stringResource(R.string.stat_correct), "${result.correctCount}/${result.totalQuestions}")
                    SummaryStat(stringResource(R.string.stat_accuracy), "$accuracy%")
                    SummaryStat(stringResource(R.string.stat_streak), "${result.bestStreak}x")
                    SummaryStat(stringResource(R.string.stat_time), "${result.durationSeconds}s")
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Surface(
            color = if (completed.syncedToCloud) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                atlasColors.goldContainer
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(14.dp)
            ) {
                Icon(
                    imageVector = if (completed.syncedToCloud) Icons.Rounded.CloudDone else Icons.Rounded.CloudQueue,
                    contentDescription = null,
                    tint = if (completed.syncedToCloud) MaterialTheme.colorScheme.onPrimaryContainer else atlasColors.onGoldContainer,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = when {
                        completed.isGuest -> stringResource(R.string.score_saved_guest)
                        completed.syncedToCloud -> stringResource(R.string.score_synced_cloud)
                        completed.queuedOffline -> stringResource(R.string.score_queued_offline)
                        else -> completed.syncError ?: stringResource(R.string.score_saved_local)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (completed.syncedToCloud) MaterialTheme.colorScheme.onPrimaryContainer else atlasColors.onGoldContainer
                )
            }
        }

        if (completed.freezesEarned > 0) {
            Spacer(Modifier.height(10.dp))
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quiz_freeze_earned_banner")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AcUnit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.streak_freeze_earned_quiz_badge, completed.freezesEarned),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        if (completed.newlyCompletedObjectivesCount > 0) {
            Spacer(Modifier.height(10.dp))
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quiz_objectives_completed_banner")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = stringResource(
                            R.string.daily_objectives_completed_round_banner,
                            completed.newlyCompletedObjectivesCount
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FilledTonalButton(
                onClick = onPlayAgain,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("quiz_play_again_button")
            ) {
                Icon(Icons.Rounded.Replay, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.play_again))
            }

            Button(
                onClick = onBackHome,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("quiz_done_button")
            ) {
                Text(stringResource(R.string.back_to_atlas))
            }
        }

        Spacer(Modifier.height(24.dp))

        // End-of-Quiz Question Review & Explanations Section
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

        Spacer(Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FilterChip(
                selected = !showMissedOnly,
                onClick = { showMissedOnly = false },
                label = { Text(stringResource(R.string.filter_all_count, completed.reviewItems.size)) },
                modifier = Modifier.testTag("review_filter_all")
            )
            FilterChip(
                selected = showMissedOnly,
                onClick = { showMissedOnly = true },
                label = { Text(stringResource(R.string.filter_missed_count, missedCount)) },
                modifier = Modifier.testTag("review_filter_missed")
            )
        }

        Spacer(Modifier.height(10.dp))

        if (displayedReviews.isEmpty()) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.flawless_round_msg),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                displayedReviews.forEach { review ->
                    QuestionReviewCard(review = review)
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun QuestionReviewCard(review: QuestionReviewItem) {
    val atlasColors = LocalAtlasColors.current
    val statusColor = if (review.wasCorrect) atlasColors.correct else atlasColors.incorrect

    Surface(
        shape = MaterialTheme.shapes.large,
        color = atlasColors.cardElevated,
        border = BorderStroke(1.dp, atlasColors.subtleBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("review_card_${review.index}")
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

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(50)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (review.wasCorrect) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = when {
                                review.wasCorrect -> "+${review.pointsEarned} pts"
                                review.timedOut -> stringResource(R.string.status_timed_out)
                                else -> stringResource(R.string.status_missed)
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor
                        )
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

            if (!review.wasCorrect && !review.userAnswer.isNullOrBlank()) {
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
private fun SummaryStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
