package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.example.R
import com.example.data.model.QuizDifficulty
import com.example.data.model.QuizMode
import com.example.data.model.WikipediaArticle
import com.example.data.repository.WikipediaRepository
import com.example.ui.theme.LocalAtlasColors
import com.example.ui.viewmodel.ArticleReaderViewModel
import com.example.ui.viewmodel.UiState
import kotlinx.coroutines.launch

private sealed interface ArticleBlock {
    data class Heading(val level: Int, val text: String) : ArticleBlock
    data class Paragraph(val text: String) : ArticleBlock
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleReaderScreen(
    viewModel: ArticleReaderViewModel,
    difficulty: QuizDifficulty,
    playMode: QuizMode,
    questionCount: Int,
    onBack: () -> Unit,
    onStartQuiz: (WikipediaArticle, String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val savedGeminiKey by viewModel.customGeminiApiKey.collectAsStateWithLifecycle()
    val savedGeminiModelName by viewModel.customGeminiModelName.collectAsStateWithLifecycle()
    val savedGeminiModelId by viewModel.customGeminiModelId.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val atlasColors = LocalAtlasColors.current
    val successArticle = (state as? UiState.Success)?.data
    val hasSavedGeminiKey = !savedGeminiKey.isNullOrBlank()
    var showOwnAiSheet by remember { mutableStateOf(false) }

    if (showOwnAiSheet && successArticle != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var apiKeyInput by remember { mutableStateOf("") }
        var pastedJson by remember { mutableStateOf("") }
        var jsonErrorVisible by remember { mutableStateOf(false) }
        val promptCopiedText = stringResource(R.string.wiki_ai_prompt_copied)

        ModalBottomSheet(
            onDismissRequest = { showOwnAiSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.testTag("wiki_own_ai_sheet")
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = stringResource(
                        if (hasSavedGeminiKey) R.string.wiki_own_ai_sheet_title else R.string.wiki_instant_only_title
                    ),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(
                        if (hasSavedGeminiKey) R.string.wiki_own_ai_sheet_subtitle else R.string.wiki_instant_only_sub,
                        successArticle.title
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (hasSavedGeminiKey) {
                    // Option 1: Personal Gemini API Key
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = atlasColors.cardElevated,
                        border = BorderStroke(1.dp, atlasColors.subtleBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Key,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.wiki_ai_option_gemini_title),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                        Text(
                            text = stringResource(R.string.wiki_ai_option_gemini_sub),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = apiKeyInput,
                            onValueChange = { apiKeyInput = it },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                autoCorrectEnabled = false
                            ),
                            label = { Text(stringResource(R.string.wiki_ai_key_input_label)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("wiki_gemini_key_input")
                        )
                        Button(
                            onClick = {
                                viewModel.saveKeyAndStartGeminiQuiz(apiKeyInput) {
                                    showOwnAiSheet = false
                                    onStartQuiz(successArticle, WikipediaRepository.SOURCE_GEMINI_KEY)
                                }
                            },
                            enabled = apiKeyInput.trim().isNotBlank(),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("wiki_gemini_generate_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.wiki_ai_save_generate_cta))
                        }
                    }
                }

                // Option 2: Copy Prompt & Paste JSON from Any AI App (Gemini, ChatGPT, Claude)
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = atlasColors.cardElevated,
                    border = BorderStroke(1.dp, atlasColors.subtleBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.ContentCopy,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.wiki_ai_option_prompt_title),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                        Text(
                            text = stringResource(R.string.wiki_ai_option_prompt_sub),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    scope.launch {
                                        val prompt = viewModel.buildPromptForArticle(
                                            article = successArticle,
                                            difficulty = difficulty,
                                            mode = playMode,
                                            count = questionCount
                                        )
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Trivia Atlas AI Prompt", prompt))
                                        Toast.makeText(context, promptCopiedText, Toast.LENGTH_LONG).show()
                                    }
                                },
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("wiki_copy_prompt_button")
                            ) {
                                Icon(Icons.Rounded.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(stringResource(R.string.wiki_ai_copy_prompt_cta))
                            }

                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        val prompt = viewModel.buildPromptForArticle(
                                            article = successArticle,
                                            difficulty = difficulty,
                                            mode = playMode,
                                            count = questionCount
                                        )
                                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, prompt)
                                        }
                                        runCatching {
                                            context.startActivity(Intent.createChooser(sendIntent, successArticle.title))
                                        }
                                    }
                                },
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("wiki_share_prompt_button")
                            ) {
                                Icon(Icons.Rounded.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(stringResource(R.string.wiki_ai_share_prompt_cta))
                            }
                        }

                        OutlinedTextField(
                            value = pastedJson,
                            onValueChange = {
                                pastedJson = it
                                jsonErrorVisible = false
                            },
                            minLines = 3,
                            maxLines = 6,
                            label = { Text(stringResource(R.string.wiki_ai_paste_json_label)) },
                            isError = jsonErrorVisible,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("wiki_paste_json_input")
                        )

                        if (jsonErrorVisible) {
                            Text(
                                text = stringResource(R.string.wiki_ai_invalid_json),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.importAiQuizJson(
                                    article = successArticle,
                                    rawResponse = pastedJson,
                                    onSuccess = {
                                        showOwnAiSheet = false
                                        onStartQuiz(successArticle, WikipediaRepository.SOURCE_IMPORTED)
                                    },
                                    onInvalid = {
                                        jsonErrorVisible = true
                                    }
                                )
                            },
                            enabled = pastedJson.isNotBlank(),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("wiki_import_json_button")
                        ) {
                            Text(stringResource(R.string.wiki_ai_import_play_cta))
                        }
                    }
                }
                }

                // Option 3: Instant Article Quiz (Algorithmic offline/no-key mode)
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = atlasColors.goldContainer.copy(alpha = 0.55f),
                    border = BorderStroke(1.dp, atlasColors.subtleBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Bolt,
                                contentDescription = null,
                                tint = atlasColors.onGoldContainer,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.wiki_ai_option_local_title),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = atlasColors.onGoldContainer
                            )
                        }
                        Text(
                            text = stringResource(R.string.wiki_ai_option_local_sub),
                            style = MaterialTheme.typography.bodySmall,
                            color = atlasColors.onGoldContainer.copy(alpha = 0.85f)
                        )
                        FilledTonalButton(
                            onClick = {
                                showOwnAiSheet = false
                                onStartQuiz(successArticle, WikipediaRepository.SOURCE_LOCAL)
                            },
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("wiki_instant_quiz_button")
                        ) {
                            Text(stringResource(R.string.wiki_ai_local_play_cta))
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("article_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back_button)
                        )
                    }
                },
                title = {
                    Text(
                        text = successArticle?.title ?: viewModel.fallbackTitle.orEmpty(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                actions = {
                    val url = successArticle?.articleUrl
                    if (!url.isNullOrBlank()) {
                        IconButton(
                            onClick = {
                                runCatching {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                }
                            },
                            modifier = Modifier.testTag("article_open_browser_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                                contentDescription = stringResource(R.string.wiki_open_browser)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (successArticle != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp,
                    border = BorderStroke(1.dp, atlasColors.subtleBorder)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Button(
                            onClick = {
                                if (hasSavedGeminiKey) {
                                    onStartQuiz(successArticle, WikipediaRepository.SOURCE_AUTO)
                                } else {
                                    showOwnAiSheet = true
                                }
                            },
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .widthIn(max = 620.dp)
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("article_start_quiz_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (hasSavedGeminiKey) {
                                    val label = savedGeminiModelName ?: savedGeminiModelId
                                    if (!label.isNullOrBlank()) {
                                        stringResource(R.string.wiki_start_ai_quiz_cta, label)
                                    } else {
                                        stringResource(R.string.wiki_start_ai_quiz_default)
                                    }
                                } else {
                                    stringResource(R.string.wiki_start_quiz_after_read)
                                },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val current = state) {
                is UiState.Loading -> {
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
                            text = stringResource(R.string.wiki_loading_full_article),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
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
                            text = if (current.message == "OFFLINE") {
                                stringResource(R.string.wiki_offline_msg)
                            } else {
                                stringResource(R.string.wiki_error_msg)
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(onClick = onBack) {
                                Text(stringResource(R.string.back_button))
                            }
                            Button(onClick = viewModel::loadArticle) {
                                Text(stringResource(R.string.retry_button))
                            }
                        }
                    }
                }

                is UiState.Success -> {
                    val article = current.data
                    val blocks = remember(article.extract) { parseWikipediaPlainText(article.extract) }
                    val wordCount = remember(article.extract) {
                        article.extract.split(Regex("\\s+")).count { it.isNotBlank() }
                    }
                    val readMinutes = remember(wordCount) { maxOf(1, (wordCount + 179) / 180) }

                    Column(
                        modifier = Modifier
                            .widthIn(max = 660.dp)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        if (!article.thumbnailUrl.isNullOrBlank()) {
                            SubcomposeAsyncImage(
                                model = article.thumbnailUrl,
                                contentDescription = article.title,
                                contentScale = ContentScale.Crop,
                                loading = {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(26.dp))
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9.5f)
                                    .clip(MaterialTheme.shapes.large)
                            )
                            Spacer(Modifier.height(16.dp))
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Public,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(Modifier.width(5.dp))
                                    Text(
                                        text = stringResource(
                                            R.string.wiki_carousel_badge,
                                            article.wikiLang.uppercase()
                                        ),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = stringResource(R.string.wiki_read_time, readMinutes),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = article.title,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        if (!article.description.isNullOrBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = article.description,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Surface(
                            color = atlasColors.goldContainer.copy(alpha = 0.7f),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.wiki_read_then_quiz_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = atlasColors.onGoldContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                            )
                        }

                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = atlasColors.subtleBorder)
                        Spacer(Modifier.height(16.dp))

                        blocks.forEach { block ->
                            when (block) {
                                is ArticleBlock.Heading -> {
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = block.text,
                                        style = if (block.level <= 2) {
                                            MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                                        } else {
                                            MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                        },
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(Modifier.height(6.dp))
                                }

                                is ArticleBlock.Paragraph -> {
                                    Text(
                                        text = block.text,
                                        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 25.sp),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(Modifier.height(12.dp))
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

private fun parseWikipediaPlainText(raw: String): List<ArticleBlock> {
    val result = mutableListOf<ArticleBlock>()
    val headingRegex = Regex("^(={2,4})\\s*(.+?)\\s*\\1$")
    val lines = raw.replace("\r\n", "\n").split("\n")
    val paragraphBuf = StringBuilder()

    fun flushParagraph() {
        val text = paragraphBuf.toString().trim()
        if (text.isNotEmpty()) {
            result.add(ArticleBlock.Paragraph(text))
        }
        paragraphBuf.clear()
    }

    for (line in lines) {
        val trimmed = line.trim()
        if (trimmed.isEmpty()) {
            flushParagraph()
            continue
        }
        val match = headingRegex.matchEntire(trimmed)
        if (match != null) {
            flushParagraph()
            val level = match.groupValues[1].length
            val headingText = match.groupValues[2].trim()
            if (headingText.isNotEmpty()) {
                result.add(ArticleBlock.Heading(level, headingText))
            }
        } else {
            if (paragraphBuf.isNotEmpty()) paragraphBuf.append(' ')
            paragraphBuf.append(trimmed)
        }
    }
    flushParagraph()
    return result
}
