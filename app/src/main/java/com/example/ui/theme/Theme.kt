package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

@Immutable
data class AtlasExtendedColors(
    val correct: Color,
    val onCorrect: Color,
    val incorrect: Color,
    val onIncorrect: Color,
    val gold: Color,
    val goldContainer: Color,
    val onGoldContainer: Color,
    val cardElevated: Color,
    val subtleBorder: Color,
    val isDark: Boolean
)

val LocalAtlasColors = staticCompositionLocalOf {
    AtlasExtendedColors(
        correct = CorrectGreenLight,
        onCorrect = Color.White,
        incorrect = IncorrectRedLight,
        onIncorrect = Color.White,
        gold = AtlasOchre,
        goldContainer = AtlasOchreContainer,
        onGoldContainer = AtlasOnOchreContainer,
        cardElevated = AtlasCardLight,
        subtleBorder = AtlasOutlineLight,
        isDark = false
    )
}

private val AtlasLightColorScheme = lightColorScheme(
    primary = AtlasEmerald,
    onPrimary = AtlasOnEmerald,
    primaryContainer = AtlasEmeraldContainer,
    onPrimaryContainer = AtlasOnEmeraldContainer,
    secondary = AtlasTerracotta,
    onSecondary = AtlasOnTerracotta,
    secondaryContainer = AtlasTerracottaContainer,
    onSecondaryContainer = AtlasOnTerracottaContainer,
    tertiary = AtlasOchre,
    onTertiary = Color.White,
    tertiaryContainer = AtlasOchreContainer,
    onTertiaryContainer = AtlasOnOchreContainer,
    background = AtlasPaper,
    onBackground = AtlasInk,
    surface = AtlasPaper,
    onSurface = AtlasInk,
    surfaceVariant = AtlasParchment,
    onSurfaceVariant = AtlasSlate,
    outline = AtlasOutlineLight,
    outlineVariant = Color(0xFFE7E1D4),
    error = IncorrectRedLight,
    onError = Color.White
)

private val AtlasDarkColorScheme = darkColorScheme(
    primary = NightSage,
    onPrimary = NightOnSage,
    primaryContainer = NightSageContainer,
    onPrimaryContainer = NightOnSageContainer,
    secondary = NightCoral,
    onSecondary = NightOnCoral,
    secondaryContainer = NightCoralContainer,
    onSecondaryContainer = NightOnCoralContainer,
    tertiary = NightGold,
    onTertiary = NightGoldContainer,
    tertiaryContainer = NightGoldContainer,
    onTertiaryContainer = NightOnGoldContainer,
    background = NightInk,
    onBackground = NightIvory,
    surface = NightInk,
    onSurface = NightIvory,
    surfaceVariant = NightSlate,
    onSurfaceVariant = NightMuted,
    outline = NightOutline,
    outlineVariant = Color(0xFF26312C),
    error = IncorrectRedDark,
    onError = Color(0xFF3E0A06)
)

private val AtlasShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun TriviaAtlasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) AtlasDarkColorScheme else AtlasLightColorScheme
    val extendedColors = if (darkTheme) {
        AtlasExtendedColors(
            correct = CorrectGreenDark,
            onCorrect = Color(0xFF06291A),
            incorrect = IncorrectRedDark,
            onIncorrect = Color(0xFF3B0C08),
            gold = NightGold,
            goldContainer = NightGoldContainer,
            onGoldContainer = NightOnGoldContainer,
            cardElevated = NightCard,
            subtleBorder = NightOutline,
            isDark = true
        )
    } else {
        AtlasExtendedColors(
            correct = CorrectGreenLight,
            onCorrect = Color.White,
            incorrect = IncorrectRedLight,
            onIncorrect = Color.White,
            gold = AtlasOchre,
            goldContainer = AtlasOchreContainer,
            onGoldContainer = AtlasOnOchreContainer,
            cardElevated = AtlasCardLight,
            subtleBorder = AtlasOutlineLight,
            isDark = false
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalAtlasColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AtlasTypography,
            shapes = AtlasShapes,
            content = content
        )
    }
}
