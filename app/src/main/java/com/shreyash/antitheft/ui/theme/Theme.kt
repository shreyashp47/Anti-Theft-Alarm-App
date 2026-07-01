package com.shreyash.antitheft.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GuardGreen,
    onPrimary = TextPrimary,
    primaryContainer = GuardGreenLight,
    onPrimaryContainer = BackgroundDark,
    secondary = GuardGreenLight,
    onSecondary = BackgroundDark,
    tertiary = AlarmRed,
    onTertiary = TextPrimary,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = BorderGray,
    outlineVariant = BorderGray,
    error = AlarmRed,
    onError = TextPrimary,
    errorContainer = AlarmRedDark,
    onErrorContainer = TextPrimary,
    surfaceVariant = SurfaceDark,
    inverseSurface = TextPrimary,
    inverseOnSurface = BackgroundDark,
)

@Composable
fun AntiTheftAlarmTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
