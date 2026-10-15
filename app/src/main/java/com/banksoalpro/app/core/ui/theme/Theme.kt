package com.banksoalpro.app.core.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Navy700,
    onPrimary = PureWhite,
    primaryContainer = Navy100,
    onPrimaryContainer = Navy900,
    secondary = Slate700,
    onSecondary = PureWhite,
    secondaryContainer = Slate100,
    onSecondaryContainer = Slate700,
    tertiary = Emerald600,
    onTertiary = PureWhite,
    tertiaryContainer = Emerald100,
    onTertiaryContainer = Emerald600,
    background = Slate50,
    onBackground = TextPrimaryLight,
    surface = PureWhite,
    onSurface = TextPrimaryLight,
    surfaceVariant = Slate100,
    onSurfaceVariant = TextSecondaryLight,
    error = Crimson500,
    onError = PureWhite,
    errorContainer = Crimson100,
    onErrorContainer = Crimson600,
    outline = Slate200
)

private val DarkColorScheme = darkColorScheme(
    primary = Navy500,
    onPrimary = PureWhite,
    primaryContainer = Navy800,
    onPrimaryContainer = Navy100,
    secondary = Slate400,
    onSecondary = Navy900,
    secondaryContainer = Slate700,
    onSecondaryContainer = Slate100,
    tertiary = Emerald500,
    onTertiary = Navy900,
    tertiaryContainer = Emerald600,
    onTertiaryContainer = PureWhite,
    background = Navy900,
    onBackground = TextPrimaryDark,
    surface = Navy800,
    onSurface = TextPrimaryDark,
    surfaceVariant = Slate700,
    onSurfaceVariant = Slate400,
    error = Crimson500,
    onError = PureWhite,
    errorContainer = Crimson600,
    onErrorContainer = PureWhite,
    outline = Slate600
)

@Composable
fun BankSoalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
