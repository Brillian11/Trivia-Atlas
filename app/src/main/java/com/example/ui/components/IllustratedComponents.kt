package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.AvatarOption
import com.example.data.model.TriviaCategory
import com.example.data.model.TriviaQuestion
import com.example.ui.theme.AtlasEmerald
import com.example.ui.theme.AtlasOchre
import com.example.ui.theme.AtlasTerracotta
import com.example.ui.theme.CatArts
import com.example.ui.theme.CatCulture
import com.example.ui.theme.CatFilm
import com.example.ui.theme.CatFood
import com.example.ui.theme.CatGeneral
import com.example.ui.theme.CatGeography
import com.example.ui.theme.CatHistory
import com.example.ui.theme.CatMusic
import com.example.ui.theme.CatPicture
import com.example.ui.theme.CatScience
import com.example.ui.theme.CatSport
import com.example.ui.theme.LocalAtlasColors
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ConnectivityStatusPill(
    isOnline: Boolean,
    modifier: Modifier = Modifier
) {
    val atlasColors = LocalAtlasColors.current
    val bgColor = if (isOnline) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
    } else {
        atlasColors.goldContainer
    }
    val fgColor = if (isOnline) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        atlasColors.onGoldContainer
    }
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(50),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Icon(
                imageVector = if (isOnline) Icons.Rounded.CloudDone else Icons.Rounded.CloudOff,
                contentDescription = null,
                tint = fgColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = stringResource(if (isOnline) R.string.online_live else R.string.offline_ready),
                style = MaterialTheme.typography.labelSmall,
                color = fgColor
            )
        }
    }
}

@Composable
fun AvatarEmblem(
    avatar: AvatarOption,
    size: Dp = 44.dp,
    modifier: Modifier = Modifier
) {
    val bgTint = when (avatar) {
        AvatarOption.COMPASS -> AtlasEmerald
        AvatarOption.OWL -> CatHistory
        AvatarOption.TELESCOPE -> CatFilm
        AvatarOption.GLOBE -> CatGeography
        AvatarOption.QUILL -> CatArts
        AvatarOption.ATOM -> CatScience
    }
    val accent = Color(0xFFFDF8EE)
    val gold = AtlasOchre

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(bgTint, bgTint.copy(alpha = 0.82f))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(size * 0.2f)) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f
            val cy = h / 2f
            val stroke = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round)

            when (avatar) {
                AvatarOption.COMPASS -> {
                    drawCircle(color = accent, radius = w * 0.44f, style = stroke)
                    val needle = Path().apply {
                        moveTo(cx, cy - h * 0.36f)
                        lineTo(cx + w * 0.14f, cy)
                        lineTo(cx, cy + h * 0.36f)
                        lineTo(cx - w * 0.14f, cy)
                        close()
                    }
                    drawPath(needle, color = gold)
                    drawCircle(color = accent, radius = w * 0.08f)
                }
                AvatarOption.OWL -> {
                    drawRoundRect(
                        color = accent,
                        topLeft = Offset(w * 0.18f, h * 0.16f),
                        size = Size(w * 0.64f, h * 0.72f),
                        cornerRadius = CornerRadius(w * 0.3f, w * 0.3f),
                        style = stroke
                    )
                    drawCircle(color = accent, radius = w * 0.12f, center = Offset(cx - w * 0.14f, cy - h * 0.06f), style = stroke)
                    drawCircle(color = accent, radius = w * 0.12f, center = Offset(cx + w * 0.14f, cy - h * 0.06f), style = stroke)
                    val beak = Path().apply {
                        moveTo(cx - w * 0.07f, cy + h * 0.05f)
                        lineTo(cx + w * 0.07f, cy + h * 0.05f)
                        lineTo(cx, cy + h * 0.18f)
                        close()
                    }
                    drawPath(beak, color = gold)
                }
                AvatarOption.TELESCOPE -> {
                    drawLine(
                        color = accent,
                        start = Offset(w * 0.2f, h * 0.65f),
                        end = Offset(w * 0.82f, h * 0.22f),
                        strokeWidth = w * 0.16f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = accent,
                        start = Offset(cx, cy),
                        end = Offset(w * 0.28f, h * 0.88f),
                        strokeWidth = w * 0.08f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = accent,
                        start = Offset(cx, cy),
                        end = Offset(w * 0.68f, h * 0.88f),
                        strokeWidth = w * 0.08f,
                        cap = StrokeCap.Round
                    )
                    drawCircle(color = gold, radius = w * 0.08f, center = Offset(w * 0.85f, h * 0.18f))
                }
                AvatarOption.GLOBE -> {
                    drawCircle(color = accent, radius = w * 0.42f, style = stroke)
                    drawOval(
                        color = gold,
                        topLeft = Offset(cx - w * 0.2f, cy - h * 0.42f),
                        size = Size(w * 0.4f, h * 0.84f),
                        style = stroke
                    )
                    drawLine(
                        color = accent,
                        start = Offset(w * 0.08f, cy),
                        end = Offset(w * 0.92f, cy),
                        strokeWidth = w * 0.07f
                    )
                }
                AvatarOption.QUILL -> {
                    val feather = Path().apply {
                        moveTo(w * 0.2f, h * 0.82f)
                        quadraticTo(w * 0.25f, h * 0.2f, w * 0.84f, h * 0.14f)
                        quadraticTo(w * 0.78f, h * 0.65f, w * 0.2f, h * 0.82f)
                        close()
                    }
                    drawPath(feather, color = accent, style = stroke)
                    drawLine(
                        color = gold,
                        start = Offset(w * 0.15f, h * 0.88f),
                        end = Offset(w * 0.78f, h * 0.2f),
                        strokeWidth = w * 0.08f,
                        cap = StrokeCap.Round
                    )
                }
                AvatarOption.ATOM -> {
                    drawCircle(color = gold, radius = w * 0.11f)
                    drawOval(
                        color = accent,
                        topLeft = Offset(w * 0.08f, cy - h * 0.18f),
                        size = Size(w * 0.84f, h * 0.36f),
                        style = stroke
                    )
                    drawOval(
                        color = accent,
                        topLeft = Offset(cx - w * 0.18f, h * 0.08f),
                        size = Size(w * 0.36f, h * 0.84f),
                        style = stroke
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryIllustrationBadge(
    category: TriviaCategory,
    size: Dp = 44.dp,
    modifier: Modifier = Modifier
) {
    val bg = category.accentColor.copy(alpha = 0.14f)
    val strokeColor = category.accentColor

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(size * 0.22f)) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f
            val cy = h / 2f
            val stroke = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round)

            when (category) {
                TriviaCategory.SCIENCE -> {
                    drawCircle(color = AtlasTerracotta, radius = w * 0.1f)
                    drawOval(strokeColor, Offset(w * 0.05f, cy - h * 0.2f), Size(w * 0.9f, h * 0.4f), style = stroke)
                    drawOval(strokeColor, Offset(cx - w * 0.2f, h * 0.05f), Size(w * 0.4f, h * 0.9f), style = stroke)
                }
                TriviaCategory.HISTORY -> {
                    drawLine(strokeColor, Offset(w * 0.15f, h * 0.22f), Offset(w * 0.85f, h * 0.22f), strokeWidth = w * 0.1f, cap = StrokeCap.Round)
                    drawLine(strokeColor, Offset(w * 0.15f, h * 0.82f), Offset(w * 0.85f, h * 0.82f), strokeWidth = w * 0.1f, cap = StrokeCap.Round)
                    listOf(0.28f, 0.5f, 0.72f).forEach { x ->
                        drawLine(strokeColor, Offset(w * x, h * 0.26f), Offset(w * x, h * 0.78f), strokeWidth = w * 0.09f)
                    }
                }
                TriviaCategory.GEOGRAPHY -> {
                    drawCircle(strokeColor, radius = w * 0.4f, style = stroke)
                    drawOval(strokeColor, Offset(cx - w * 0.18f, cy - h * 0.4f), Size(w * 0.36f, h * 0.8f), style = stroke)
                    drawLine(strokeColor, Offset(w * 0.1f, cy), Offset(w * 0.9f, cy), strokeWidth = w * 0.08f)
                }
                TriviaCategory.ARTS_AND_LITERATURE -> {
                    val palette = Path().apply {
                        moveTo(w * 0.2f, h * 0.8f)
                        quadraticTo(w * 0.18f, h * 0.22f, w * 0.84f, h * 0.16f)
                        quadraticTo(w * 0.78f, h * 0.68f, w * 0.2f, h * 0.8f)
                    }
                    drawPath(palette, strokeColor, style = stroke)
                    drawCircle(AtlasOchre, w * 0.09f, Offset(w * 0.54f, h * 0.45f))
                }
                TriviaCategory.FILM_AND_TV -> {
                    drawRoundRect(strokeColor, Offset(w * 0.1f, h * 0.22f), Size(w * 0.8f, h * 0.58f), CornerRadius(w * 0.12f, w * 0.12f), style = stroke)
                    val play = Path().apply {
                        moveTo(w * 0.42f, h * 0.37f)
                        lineTo(w * 0.65f, cy)
                        lineTo(w * 0.42f, h * 0.65f)
                        close()
                    }
                    drawPath(play, strokeColor)
                }
                TriviaCategory.MUSIC -> {
                    drawCircle(strokeColor, w * 0.14f, Offset(w * 0.3f, h * 0.72f))
                    drawCircle(strokeColor, w * 0.14f, Offset(w * 0.72f, h * 0.64f))
                    drawLine(strokeColor, Offset(w * 0.42f, h * 0.7f), Offset(w * 0.42f, h * 0.22f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
                    drawLine(strokeColor, Offset(w * 0.84f, h * 0.62f), Offset(w * 0.84f, h * 0.16f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
                    drawLine(strokeColor, Offset(w * 0.42f, h * 0.22f), Offset(w * 0.84f, h * 0.16f), strokeWidth = w * 0.1f, cap = StrokeCap.Round)
                }
                TriviaCategory.SPORT_AND_LEISURE -> {
                    drawArc(strokeColor, 0f, 180f, false, Offset(w * 0.22f, h * 0.16f), Size(w * 0.56f, h * 0.52f), style = stroke)
                    drawLine(strokeColor, Offset(w * 0.22f, h * 0.42f), Offset(w * 0.22f, h * 0.18f), strokeWidth = w * 0.09f)
                    drawLine(strokeColor, Offset(w * 0.78f, h * 0.42f), Offset(w * 0.78f, h * 0.18f), strokeWidth = w * 0.09f)
                    drawLine(strokeColor, Offset(w * 0.22f, h * 0.18f), Offset(w * 0.78f, h * 0.18f), strokeWidth = w * 0.09f)
                    drawLine(strokeColor, Offset(cx, h * 0.68f), Offset(cx, h * 0.86f), strokeWidth = w * 0.1f)
                    drawLine(strokeColor, Offset(w * 0.32f, h * 0.86f), Offset(w * 0.68f, h * 0.86f), strokeWidth = w * 0.1f, cap = StrokeCap.Round)
                }
                TriviaCategory.FOOD_AND_DRINK -> {
                    drawRoundRect(strokeColor, Offset(w * 0.2f, h * 0.35f), Size(w * 0.52f, h * 0.46f), CornerRadius(w * 0.14f, w * 0.14f), style = stroke)
                    drawArc(strokeColor, -90f, 180f, false, Offset(w * 0.62f, h * 0.42f), Size(w * 0.24f, h * 0.26f), style = stroke)
                    drawLine(AtlasOchre, Offset(w * 0.36f, h * 0.16f), Offset(w * 0.36f, h * 0.26f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
                    drawLine(AtlasOchre, Offset(w * 0.54f, h * 0.14f), Offset(w * 0.54f, h * 0.24f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
                }
                TriviaCategory.SOCIETY_AND_CULTURE -> {
                    drawOval(strokeColor, Offset(w * 0.24f, h * 0.22f), Size(w * 0.52f, h * 0.58f), style = stroke)
                    drawLine(strokeColor, Offset(w * 0.24f, cy), Offset(w * 0.76f, cy), strokeWidth = w * 0.08f)
                    drawLine(AtlasOchre, Offset(cx, h * 0.1f), Offset(cx, h * 0.22f), strokeWidth = w * 0.09f)
                    drawLine(AtlasOchre, Offset(cx, h * 0.8f), Offset(cx, h * 0.92f), strokeWidth = w * 0.09f)
                }
                TriviaCategory.TECHNOLOGY -> {
                    drawRoundRect(
                        strokeColor,
                        Offset(w * 0.24f, h * 0.24f),
                        Size(w * 0.52f, h * 0.52f),
                        CornerRadius(w * 0.1f, w * 0.1f),
                        style = stroke
                    )
                    drawCircle(AtlasOchre, w * 0.1f, Offset(cx, cy))
                    listOf(0.38f, 0.62f).forEach { pos ->
                        drawLine(strokeColor, Offset(w * pos, h * 0.08f), Offset(w * pos, h * 0.22f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
                        drawLine(strokeColor, Offset(w * pos, h * 0.78f), Offset(w * pos, h * 0.92f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
                        drawLine(strokeColor, Offset(w * 0.08f, h * pos), Offset(w * 0.22f, h * pos), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
                        drawLine(strokeColor, Offset(w * 0.78f, h * pos), Offset(w * 0.92f, h * pos), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
                    }
                }
                TriviaCategory.PICTURE_ATLAS -> {
                    drawRoundRect(strokeColor, Offset(w * 0.12f, h * 0.18f), Size(w * 0.76f, h * 0.64f), CornerRadius(w * 0.12f, w * 0.12f), style = stroke)
                    val mountain = Path().apply {
                        moveTo(w * 0.2f, h * 0.7f)
                        lineTo(w * 0.42f, h * 0.42f)
                        lineTo(w * 0.58f, h * 0.6f)
                        lineTo(w * 0.72f, h * 0.48f)
                        lineTo(w * 0.82f, h * 0.7f)
                    }
                    drawPath(mountain, strokeColor, style = stroke)
                    drawCircle(AtlasOchre, w * 0.08f, Offset(w * 0.68f, h * 0.32f))
                }
                else -> {
                    drawCircle(strokeColor, w * 0.4f, style = stroke)
                    drawCircle(AtlasOchre, w * 0.12f)
                }
            }
        }
    }
}

@Composable
fun PictureQuizMediaFrame(
    question: TriviaQuestion,
    modifier: Modifier = Modifier
) {
    val atlasColors = LocalAtlasColors.current
    Surface(
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, atlasColors.subtleBorder),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 10f)
    ) {
        when {
            question.imageDrawableRes != null -> {
                Image(
                    painter = painterResource(id = question.imageDrawableRes),
                    contentDescription = "Visual clue for trivia question",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            !question.imageUrl.isNullOrBlank() -> {
                AsyncImage(
                    model = question.imageUrl,
                    contentDescription = "Visual clue for trivia question",
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.img_hero_atlas),
                    error = painterResource(id = R.drawable.img_hero_atlas),
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                IllustratedPicturePlate(
                    tag = question.illustrationTag ?: "compass_rose",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun IllustratedPicturePlate(
    tag: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        when (tag) {
            "pyramids" -> {
                drawRect(
                    Brush.verticalGradient(listOf(Color(0xFFF6D7B0), Color(0xFFE59A64)))
                )
                drawCircle(Color(0xFFFFF0C2), radius = h * 0.14f, center = Offset(w * 0.76f, h * 0.25f))
                val p1 = Path().apply {
                    moveTo(w * 0.12f, h * 0.84f)
                    lineTo(w * 0.38f, h * 0.34f)
                    lineTo(w * 0.64f, h * 0.84f)
                    close()
                }
                val p2 = Path().apply {
                    moveTo(w * 0.48f, h * 0.84f)
                    lineTo(w * 0.72f, h * 0.44f)
                    lineTo(w * 0.92f, h * 0.84f)
                    close()
                }
                drawPath(p1, Color(0xFFC28246))
                drawPath(p2, Color(0xFFA96935))
                drawRect(Color(0xFF935A2C), topLeft = Offset(0f, h * 0.84f), size = Size(w, h * 0.16f))
            }
            "mount_fuji" -> {
                drawRect(
                    Brush.verticalGradient(listOf(Color(0xFFDDE8E6), Color(0xFFF4E4D0)))
                )
                drawCircle(Color(0xFFC85A3A), radius = h * 0.13f, center = Offset(w * 0.25f, h * 0.28f))
                val mountain = Path().apply {
                    moveTo(w * 0.08f, h * 0.88f)
                    quadraticTo(w * 0.36f, h * 0.66f, w * 0.44f, h * 0.28f)
                    lineTo(w * 0.56f, h * 0.28f)
                    quadraticTo(w * 0.64f, h * 0.66f, w * 0.92f, h * 0.88f)
                    close()
                }
                drawPath(mountain, Color(0xFF284654))
                val snow = Path().apply {
                    moveTo(w * 0.44f, h * 0.28f)
                    lineTo(w * 0.56f, h * 0.28f)
                    lineTo(w * 0.61f, h * 0.47f)
                    lineTo(w * 0.53f, h * 0.42f)
                    lineTo(w * 0.48f, h * 0.49f)
                    lineTo(w * 0.39f, h * 0.45f)
                    close()
                }
                drawPath(snow, Color(0xFFFAF8F3))
            }
            "eiffel" -> {
                drawRect(
                    Brush.verticalGradient(listOf(Color(0xFF233044), Color(0xFF566B82)))
                )
                val iron = Color(0xFFEADBC4)
                val stroke = Stroke(width = w * 0.014f, cap = StrokeCap.Round)
                val body = Path().apply {
                    moveTo(cx, h * 0.12f)
                    lineTo(cx - w * 0.04f, h * 0.48f)
                    quadraticTo(cx - w * 0.12f, h * 0.72f, cx - w * 0.22f, h * 0.88f)
                    moveTo(cx, h * 0.12f)
                    lineTo(cx + w * 0.04f, h * 0.48f)
                    quadraticTo(cx + w * 0.12f, h * 0.72f, cx + w * 0.22f, h * 0.88f)
                }
                drawPath(body, iron, style = stroke)
                drawLine(iron, Offset(cx - w * 0.14f, h * 0.68f), Offset(cx + w * 0.14f, h * 0.68f), strokeWidth = w * 0.018f)
                drawLine(iron, Offset(cx - w * 0.08f, h * 0.48f), Offset(cx + w * 0.08f, h * 0.48f), strokeWidth = w * 0.018f)
                drawArc(iron, 180f, 180f, false, Offset(cx - w * 0.1f, h * 0.72f), Size(w * 0.2f, h * 0.24f), style = stroke)
                drawRect(Color(0xFF192231), Offset(0f, h * 0.88f), Size(w, h * 0.12f))
            }
            "eclipse" -> {
                drawRect(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF24305E), Color(0xFF0B0E1D)),
                        center = Offset(cx, cy),
                        radius = w * 0.6f
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFD178), Color(0x88FF9E44), Color.Transparent),
                        center = Offset(cx, cy),
                        radius = h * 0.42f
                    ),
                    radius = h * 0.42f,
                    center = Offset(cx, cy)
                )
                drawCircle(Color(0xFF0A0D18), radius = h * 0.24f, center = Offset(cx, cy))
            }
            "parthenon" -> {
                drawRect(Brush.verticalGradient(listOf(Color(0xFFE7EFF4), Color(0xFFF6ECDC))))
                val stone = Color(0xFF8C7B65)
                val pediment = Path().apply {
                    moveTo(w * 0.16f, h * 0.34f)
                    lineTo(cx, h * 0.16f)
                    lineTo(w * 0.84f, h * 0.34f)
                    close()
                }
                drawPath(pediment, stone)
                drawRect(Color(0xFFB3A189), Offset(w * 0.18f, h * 0.34f), Size(w * 0.64f, h * 0.06f))
                listOf(0.23f, 0.34f, 0.45f, 0.56f, 0.67f, 0.77f).forEach { x ->
                    drawRect(Color(0xFFD5C8B5), Offset(w * x - w * 0.02f, h * 0.4f), Size(w * 0.04f, h * 0.4f))
                }
                drawRect(stone, Offset(w * 0.14f, h * 0.8f), Size(w * 0.72f, h * 0.08f))
            }
            "prism" -> {
                drawRect(Color(0xFF151924))
                drawLine(Color.White, Offset(0f, h * 0.58f), Offset(cx - w * 0.06f, h * 0.48f), strokeWidth = h * 0.03f)
                val spectrum = listOf(
                    Color(0xFFE6534B),
                    Color(0xFFF19B38),
                    Color(0xFFF4D35E),
                    Color(0xFF52B788),
                    Color(0xFF4EA8DE),
                    Color(0xFF7B61FF)
                )
                spectrum.forEachIndexed { idx, col ->
                    val targetY = h * (0.36f + idx * 0.075f)
                    drawLine(col, Offset(cx + w * 0.06f, h * 0.48f), Offset(w, targetY), strokeWidth = h * 0.028f)
                }
                val tri = Path().apply {
                    moveTo(cx, h * 0.22f)
                    lineTo(cx + w * 0.16f, h * 0.76f)
                    lineTo(cx - w * 0.16f, h * 0.76f)
                    close()
                }
                drawPath(tri, Color(0x66BFE6FF))
                drawPath(tri, Color(0xFFE0F4FF), style = Stroke(width = w * 0.01f))
            }
            "compass_rose" -> {
                drawRect(Color(0xFFF4EDE0))
                drawCircle(Color(0xFF1B4D3E), radius = h * 0.36f, center = Offset(cx, cy), style = Stroke(width = h * 0.015f))
                for (i in 0 until 8) {
                    val angle = Math.toRadians((i * 45).toDouble())
                    val rOuter = if (i % 2 == 0) h * 0.34f else h * 0.22f
                    val tip = Offset(cx + (cos(angle) * rOuter).toFloat(), cy + (sin(angle) * rOuter).toFloat())
                    val leftAngle = angle - 0.35
                    val rightAngle = angle + 0.35
                    val rInner = h * 0.08f
                    val p = Path().apply {
                        moveTo(tip.x, tip.y)
                        lineTo(cx + (cos(leftAngle) * rInner).toFloat(), cy + (sin(leftAngle) * rInner).toFloat())
                        lineTo(cx, cy)
                        lineTo(cx + (cos(rightAngle) * rInner).toFloat(), cy + (sin(rightAngle) * rInner).toFloat())
                        close()
                    }
                    drawPath(p, if (i == 6) Color(0xFFC85A3A) else if (i % 2 == 0) Color(0xFF1B4D3E) else Color(0xFFD49B35))
                }
                drawCircle(Color(0xFFF4EDE0), radius = h * 0.04f, center = Offset(cx, cy))
            }
            "colosseum" -> {
                drawRect(Brush.verticalGradient(listOf(Color(0xFFEBD9C1), Color(0xFFD9AE83))))
                val stone = Color(0xFFC59B6D)
                val darkArch = Color(0xFF5D432C)
                drawRoundRect(stone, Offset(w * 0.14f, h * 0.34f), Size(w * 0.72f, h * 0.5f), CornerRadius(w * 0.06f, w * 0.06f))
                drawRect(Color(0xFFEBD9C1), Offset(w * 0.52f, h * 0.28f), Size(w * 0.36f, h * 0.14f))
                for (row in 0..1) {
                    val y = h * (0.42f + row * 0.2f)
                    for (col in 0..4) {
                        val x = w * (0.21f + col * 0.13f)
                        drawRoundRect(darkArch, Offset(x, y), Size(w * 0.07f, h * 0.14f), CornerRadius(w * 0.035f, w * 0.035f))
                    }
                }
                drawRect(Color(0xFF7D6548), Offset(0f, h * 0.84f), Size(w, h * 0.16f))
            }
            "golden_gate" -> {
                drawRect(Brush.verticalGradient(listOf(Color(0xFFD9E6EC), Color(0xFFF2D6BD))))
                drawRect(Color(0xFF2F5E73), Offset(0f, h * 0.74f), Size(w, h * 0.26f))
                val bridgeRed = Color(0xFFC04331)
                listOf(0.28f, 0.72f).forEach { tx ->
                    drawRect(bridgeRed, Offset(w * tx - w * 0.02f, h * 0.22f), Size(w * 0.04f, h * 0.52f))
                }
                val cable = Path().apply {
                    moveTo(0f, h * 0.56f)
                    quadraticTo(w * 0.14f, h * 0.52f, w * 0.28f, h * 0.22f)
                    quadraticTo(cx, h * 0.64f, w * 0.72f, h * 0.22f)
                    quadraticTo(w * 0.86f, h * 0.52f, w, h * 0.56f)
                }
                drawPath(cable, bridgeRed, style = Stroke(width = w * 0.014f))
                drawLine(bridgeRed, Offset(0f, h * 0.62f), Offset(w, h * 0.62f), strokeWidth = h * 0.032f)
            }
            "volcano" -> {
                drawRect(Brush.verticalGradient(listOf(Color(0xFF2D2538), Color(0xFF703B3D))))
                val cone = Path().apply {
                    moveTo(w * 0.1f, h * 0.9f)
                    lineTo(w * 0.4f, h * 0.4f)
                    lineTo(w * 0.6f, h * 0.4f)
                    lineTo(w * 0.9f, h * 0.9f)
                    close()
                }
                drawPath(cone, Color(0xFF3A3138))
                val lava = Path().apply {
                    moveTo(w * 0.43f, h * 0.4f)
                    lineTo(w * 0.57f, h * 0.4f)
                    lineTo(cx + w * 0.04f, h * 0.68f)
                    lineTo(cx - w * 0.02f, h * 0.62f)
                    close()
                }
                drawPath(lava, Color(0xFFFF6D2E))
                drawCircle(Color(0x55C7B8C4), radius = h * 0.14f, center = Offset(cx, h * 0.24f))
                drawCircle(Color(0x44DDD3DC), radius = h * 0.11f, center = Offset(cx + w * 0.08f, h * 0.16f))
            }
            "crescent_moon" -> {
                drawRect(Brush.verticalGradient(listOf(Color(0xFF0E172E), Color(0xFF1F2F56))))
                listOf(Offset(0.2f, 0.24f), Offset(0.8f, 0.22f), Offset(0.74f, 0.68f), Offset(0.26f, 0.74f), Offset(0.62f, 0.34f)).forEach { pt ->
                    drawCircle(Color(0xFFFFF4D2), radius = h * 0.015f, center = Offset(w * pt.x, h * pt.y))
                }
                drawCircle(Color(0xFFF6DF96), radius = h * 0.26f, center = Offset(cx, cy))
                drawCircle(Color(0xFF162342), radius = h * 0.23f, center = Offset(cx + w * 0.08f, cy - h * 0.04f))
            }
            "torii_gate" -> {
                drawRect(Brush.verticalGradient(listOf(Color(0xFFF5DFC8), Color(0xFFE7B69B))))
                drawRect(Color(0xFF3E6C7A), Offset(0f, h * 0.78f), Size(w, h * 0.22f))
                val vermilion = Color(0xFFC93B2B)
                drawRect(vermilion, Offset(w * 0.3f, h * 0.28f), Size(w * 0.055f, h * 0.56f))
                drawRect(vermilion, Offset(w * 0.645f, h * 0.28f), Size(w * 0.055f, h * 0.56f))
                drawRect(vermilion, Offset(w * 0.2f, h * 0.24f), Size(w * 0.6f, h * 0.06f))
                drawRect(vermilion, Offset(w * 0.24f, h * 0.38f), Size(w * 0.52f, h * 0.045f))
                drawRect(Color(0xFF211E1D), Offset(w * 0.18f, h * 0.21f), Size(w * 0.64f, h * 0.035f))
            }
            "sundial" -> {
                drawRect(Color(0xFFF1E8D6))
                drawCircle(Color(0xFFC8B48E), radius = h * 0.36f, center = Offset(cx, cy + h * 0.04f))
                drawCircle(Color(0xFF7D6644), radius = h * 0.36f, center = Offset(cx, cy + h * 0.04f), style = Stroke(width = w * 0.016f))
                for (i in -3..3) {
                    val ang = Math.toRadians((-90 + i * 24).toDouble())
                    drawLine(
                        Color(0xFF5D482A),
                        Offset(cx, cy + h * 0.04f),
                        Offset(cx + (cos(ang) * h * 0.31f).toFloat(), cy + h * 0.04f + (sin(ang) * h * 0.31f).toFloat()),
                        strokeWidth = w * 0.008f
                    )
                }
                val gnomon = Path().apply {
                    moveTo(cx, cy + h * 0.04f)
                    lineTo(cx, cy - h * 0.24f)
                    lineTo(cx + w * 0.14f, cy + h * 0.04f)
                    close()
                }
                drawPath(gnomon, Color(0xFF473721))
            }
            "lighthouse" -> {
                drawRect(Brush.verticalGradient(listOf(Color(0xFF1A314A), Color(0xFF396182))))
                drawRect(Color(0xFF1F4968), Offset(0f, h * 0.8f), Size(w, h * 0.2f))
                val beam = Path().apply {
                    moveTo(cx, h * 0.24f)
                    lineTo(w, h * 0.1f)
                    lineTo(w, h * 0.42f)
                    close()
                }
                drawPath(beam, Color(0x66FFE885))
                val tower = Path().apply {
                    moveTo(cx - w * 0.08f, h * 0.8f)
                    lineTo(cx - w * 0.05f, h * 0.3f)
                    lineTo(cx + w * 0.05f, h * 0.3f)
                    lineTo(cx + w * 0.08f, h * 0.8f)
                    close()
                }
                drawPath(tower, Color(0xFFF6F2E9))
                drawRect(Color(0xFFC94435), Offset(cx - w * 0.06f, h * 0.46f), Size(w * 0.12f, h * 0.1f))
                drawCircle(Color(0xFFFFE57F), radius = h * 0.06f, center = Offset(cx, h * 0.24f))
            }
            "windmill" -> {
                drawRect(Brush.verticalGradient(listOf(Color(0xFFBDD8E8), Color(0xFFEBF1E2))))
                drawRect(Color(0xFF5D8A52), Offset(0f, h * 0.78f), Size(w, h * 0.22f))
                val mill = Path().apply {
                    moveTo(cx - w * 0.1f, h * 0.78f)
                    lineTo(cx - w * 0.06f, h * 0.34f)
                    lineTo(cx + w * 0.06f, h * 0.34f)
                    lineTo(cx + w * 0.1f, h * 0.78f)
                    close()
                }
                drawPath(mill, Color(0xFF8C5339))
                val hub = Offset(cx, h * 0.4f)
                for (k in 0..3) {
                    val ang = Math.toRadians((45 + k * 90).toDouble())
                    drawLine(
                        Color(0xFFF8F4EC),
                        hub,
                        Offset(hub.x + (cos(ang) * h * 0.32f).toFloat(), hub.y + (sin(ang) * h * 0.32f).toFloat()),
                        strokeWidth = w * 0.025f,
                        cap = StrokeCap.Round
                    )
                }
                drawCircle(Color(0xFF42281D), radius = h * 0.035f, center = hub)
            }
            "hourglass" -> {
                drawRect(Color(0xFFEDE4D4))
                val frame = Color(0xFF5B3F29)
                drawRect(frame, Offset(cx - w * 0.16f, h * 0.16f), Size(w * 0.32f, h * 0.05f))
                drawRect(frame, Offset(cx - w * 0.16f, h * 0.79f), Size(w * 0.32f, h * 0.05f))
                val glass = Path().apply {
                    moveTo(cx - w * 0.13f, h * 0.21f)
                    lineTo(cx + w * 0.13f, h * 0.21f)
                    lineTo(cx, cy)
                    lineTo(cx + w * 0.13f, h * 0.79f)
                    lineTo(cx - w * 0.13f, h * 0.79f)
                    lineTo(cx, cy)
                    close()
                }
                drawPath(glass, Color(0xFFD5E9F0))
                val sandBottom = Path().apply {
                    moveTo(cx - w * 0.1f, h * 0.78f)
                    lineTo(cx, h * 0.62f)
                    lineTo(cx + w * 0.1f, h * 0.78f)
                    close()
                }
                drawPath(sandBottom, Color(0xFFD69F4C))
                drawLine(Color(0xFFD69F4C), Offset(cx, cy), Offset(cx, h * 0.64f), strokeWidth = w * 0.01f)
            }
            "olympic_torch" -> {
                drawRect(Brush.verticalGradient(listOf(Color(0xFF1F3647), Color(0xFF3E6A75))))
                val flameOuter = Path().apply {
                    moveTo(cx, h * 0.12f)
                    quadraticTo(cx + w * 0.11f, h * 0.28f, cx + w * 0.06f, h * 0.44f)
                    lineTo(cx - w * 0.06f, h * 0.44f)
                    quadraticTo(cx - w * 0.11f, h * 0.28f, cx, h * 0.12f)
                    close()
                }
                drawPath(flameOuter, Color(0xFFF27D32))
                val torchBody = Path().apply {
                    moveTo(cx - w * 0.07f, h * 0.44f)
                    lineTo(cx + w * 0.07f, h * 0.44f)
                    lineTo(cx + w * 0.025f, h * 0.86f)
                    lineTo(cx - w * 0.025f, h * 0.86f)
                    close()
                }
                drawPath(torchBody, Color(0xFFE5C87B))
            }
            "chess_knight" -> {
                drawRect(Color(0xFFE8DFCE))
                drawRect(Color(0xFF534031), Offset(cx - w * 0.14f, h * 0.76f), Size(w * 0.28f, h * 0.08f))
                val knight = Path().apply {
                    moveTo(cx - w * 0.1f, h * 0.76f)
                    lineTo(cx - w * 0.08f, h * 0.45f)
                    lineTo(cx - w * 0.15f, h * 0.42f)
                    lineTo(cx - w * 0.04f, h * 0.24f)
                    lineTo(cx, h * 0.16f)
                    quadraticTo(cx + w * 0.14f, h * 0.28f, cx + w * 0.1f, h * 0.76f)
                    close()
                }
                drawPath(knight, Color(0xFF2B241E))
            }
            "folding_fan" -> {
                drawRect(Color(0xFFF6EBDC))
                val fanArc = Color(0xFFB94338)
                drawArc(
                    color = fanArc,
                    startAngle = 200f,
                    sweepAngle = 140f,
                    useCenter = true,
                    topLeft = Offset(cx - h * 0.42f, cy - h * 0.18f),
                    size = Size(h * 0.84f, h * 0.84f)
                )
                drawArc(
                    color = Color(0xFFF6EBDC),
                    startAngle = 200f,
                    sweepAngle = 140f,
                    useCenter = true,
                    topLeft = Offset(cx - h * 0.16f, cy + h * 0.08f),
                    size = Size(h * 0.32f, h * 0.32f)
                )
                for (i in 0..6) {
                    val ang = Math.toRadians((205 + i * 21.5).toDouble())
                    drawLine(
                        Color(0xFF6B4C35),
                        Offset(cx, cy + h * 0.24f),
                        Offset(cx + (cos(ang) * h * 0.4f).toFloat(), cy + h * 0.24f + (sin(ang) * h * 0.4f).toFloat()),
                        strokeWidth = w * 0.008f
                    )
                }
            }
            else -> {
                // Treble clef on warm parchment staff
                drawRect(Color(0xFFF6EFE2))
                val staffColor = Color(0xFF8E8577)
                listOf(0.32f, 0.41f, 0.50f, 0.59f, 0.68f).forEach { y ->
                    drawLine(staffColor, Offset(w * 0.12f, h * y), Offset(w * 0.88f, h * y), strokeWidth = h * 0.012f)
                }
                val clef = Path().apply {
                    moveTo(cx + w * 0.01f, h * 0.82f)
                    lineTo(cx + w * 0.01f, h * 0.18f)
                    cubicTo(cx + w * 0.12f, h * 0.18f, cx + w * 0.12f, h * 0.42f, cx - w * 0.08f, h * 0.48f)
                    cubicTo(cx - w * 0.14f, h * 0.52f, cx - w * 0.1f, h * 0.7f, cx + w * 0.04f, h * 0.68f)
                    cubicTo(cx + w * 0.12f, h * 0.66f, cx + w * 0.11f, h * 0.5f, cx, h * 0.52f)
                }
                drawPath(clef, Color(0xFF1B4D3E), style = Stroke(width = h * 0.036f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }
    }
}
