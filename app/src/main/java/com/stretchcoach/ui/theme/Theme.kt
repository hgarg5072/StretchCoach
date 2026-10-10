package com.stretchcoach.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.stretchcoach.settings.DarkModePreference

private val LightColors = lightColorScheme(
    primary = Color(0xFF006B5F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA9F2E3),
    onPrimaryContainer = Color(0xFF00201C),
    secondary = Color(0xFFB84A62),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFD9DF),
    onSecondaryContainer = Color(0xFF400013),
    tertiary = Color(0xFF5C5BC0),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE2DFFF),
    onTertiaryContainer = Color(0xFF17115B),
    background = Color(0xFFF7FAF7),
    onBackground = Color(0xFF171D1B),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF171D1B),
    surfaceVariant = Color(0xFFDCE7E2),
    onSurfaceVariant = Color(0xFF40504B),
    outline = Color(0xFF70807A)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7EDCCF),
    onPrimary = Color(0xFF003731),
    primaryContainer = Color(0xFF005047),
    onPrimaryContainer = Color(0xFFA9F2E3),
    secondary = Color(0xFFFFB1BF),
    onSecondary = Color(0xFF650722),
    secondaryContainer = Color(0xFF8F3047),
    onSecondaryContainer = Color(0xFFFFD9DF),
    tertiary = Color(0xFFC6C1FF),
    onTertiary = Color(0xFF2E2A78),
    tertiaryContainer = Color(0xFF4443A6),
    onTertiaryContainer = Color(0xFFE2DFFF),
    background = Color(0xFF101816),
    onBackground = Color(0xFFE0E6E2),
    surface = Color(0xFF17211F),
    onSurface = Color(0xFFE0E6E2),
    surfaceVariant = Color(0xFF3D4A46),
    onSurfaceVariant = Color(0xFFBFCCC7),
    outline = Color(0xFF899691)
)

@Composable
fun StretchCoachTheme(
    darkModePreference: DarkModePreference,
    content: @Composable () -> Unit
) {
    val darkTheme = when (darkModePreference) {
        DarkModePreference.System -> isSystemInDarkTheme()
        DarkModePreference.Light -> false
        DarkModePreference.Dark -> true
    }
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography,
        content = content
    )
}
