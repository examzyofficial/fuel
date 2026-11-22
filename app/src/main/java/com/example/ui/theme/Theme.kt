package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = Color.White,
    primaryContainer = NavyPrimary,
    onPrimaryContainer = NavyOnContainer,
    secondary = RoyalSecondary,
    onSecondary = Color.White,
    secondaryContainer = SurfaceContainerHigh,
    onSecondaryContainer = RoyalSecondary,
    tertiary = AmberAccent,
    onTertiary = NavyPrimary,
    tertiaryContainer = AmberFixed,
    onTertiaryContainer = AmberOnFixed,
    background = CanvasBackground,
    onBackground = TextPrimary,
    surface = SurfacePure,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceContainerHigh,
    onSurfaceVariant = TextSecondary,
    surfaceContainerLowest = SurfacePure,
    surfaceContainerLow = SurfaceLow,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    outline = OutlineStrong,
    outlineVariant = OutlineSubtle,
    error = AlertRed,
    onError = Color.White,
    errorContainer = AlertRedBg,
    onErrorContainer = AlertRed
)

private val DarkColorScheme = darkColorScheme(
    primary = NavyOnContainer,
    onPrimary = NavyPrimary,
    primaryContainer = DarkSurfaceHigh,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF708CFD),
    onSecondary = NavyDeep,
    secondaryContainer = DarkSurfaceHighest,
    onSecondaryContainer = Color(0xFFDDE1FF),
    tertiary = AmberAccent,
    onTertiary = NavyPrimary,
    tertiaryContainer = Color(0xFF423000),
    onTertiaryContainer = AmberFixed,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceHigh,
    onSurfaceVariant = DarkTextSecondary,
    surfaceContainerLowest = DarkSurface,
    surfaceContainerLow = DarkSurface,
    surfaceContainer = DarkSurfaceHigh,
    surfaceContainerHigh = DarkSurfaceHigh,
    surfaceContainerHighest = DarkSurfaceHighest,
    outline = DarkOutline,
    outlineVariant = DarkOutline,
    error = Color(0xFFF87171),
    onError = NavyDeep,
    errorContainer = Color(0xFF451212),
    onErrorContainer = Color(0xFFFECACA)
)

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun FuelDensityTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
