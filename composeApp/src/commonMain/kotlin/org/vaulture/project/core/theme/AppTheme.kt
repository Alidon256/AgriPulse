package org.vaulture.project.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import mindsetpulse.composeapp.generated.resources.Res
import mindsetpulse.composeapp.generated.resources.poppins_bold
import mindsetpulse.composeapp.generated.resources.poppins_medium
import mindsetpulse.composeapp.generated.resources.poppins_regular
import org.jetbrains.compose.resources.Font


@Composable
fun PoppinsFontFamily(): FontFamily = FontFamily(
    Font(Res.font.poppins_regular, weight = FontWeight.Normal),
    Font(Res.font.poppins_bold, weight = FontWeight.Bold),
    Font(Res.font.poppins_medium, weight = FontWeight.Medium)
)

@Composable
fun PoppinsTypography(): Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = PoppinsFontFamily(),
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = PoppinsFontFamily(),
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = PoppinsFontFamily(),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = PoppinsFontFamily(),
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = PoppinsFontFamily(),
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
)

private val NatureLightColors = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFFF57C00),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0B2),
    onSecondaryContainer = Color(0xFFE65100),
    tertiary = Color(0xFF00796B),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB2DFDB),
    onTertiaryContainer = Color(0xFF004D40),
    error = Color(0xFFD32F2F),
    background = Color(0xFFF4F6F4),
    onBackground = Color(0xFF1B1C1B),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1B1C1B),
    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFF424242)
)

private val NatureDarkColors = darkColorScheme(
    primary = Color(0xFF4CAF50),
    onPrimary = Color(0xFF003300),
    primaryContainer = Color(0xFF1B5E20),
    onPrimaryContainer = Color(0xFFC8E6C9),
    secondary = Color(0xFFFFB74D),
    onSecondary = Color(0xFF4E2600),
    secondaryContainer = Color(0xFFE65100),
    onSecondaryContainer = Color(0xFFFFE0B2),
    tertiary = Color(0xFF80CBC4),
    onTertiary = Color(0xFF003730),
    tertiaryContainer = Color(0xFF004D40),
    onTertiaryContainer = Color(0xFFB2DFDB),
    error = Color(0xFFFFB4AB),
    background = Color(0xFF121412),
    onBackground = Color(0xFFE2E3E2),
    surface = Color(0xFF1C1E1C),
    onSurface = Color(0xFFE2E3E2),
    surfaceVariant = Color(0xFF2A2D2A),
    onSurfaceVariant = Color(0xFFC2C4C2)
)

private val OceanLightColors = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFF00796B),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = Color(0xFF004D40),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF0A1014),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0A1014),
    surfaceVariant = Color(0xFFE0E0E0).copy(alpha = 0.5f),
    onSurfaceVariant = Color(0xFF0A1014).copy(alpha = 0.7f),
    error = Color(0xFFD32F2F),
    onError = Color(0xFFFFFFFF),
    outline = Color(0xFF2E7D32).copy(alpha = 0.5f)
)

private val OceanDarkColors = darkColorScheme(
    primary = Color(0xFF4CAF50),
    onPrimary = Color(0xFF003300),
    primaryContainer = Color(0xFF1B5E20),
    onPrimaryContainer = Color(0xFFC8E6C9),
    secondary = Color(0xFF80CBC4),
    onSecondary = Color(0xFF003730),
    secondaryContainer = Color(0xFF004D40),
    onSecondaryContainer = Color(0xFFB2DFDB),
    background = Color(0xFF121412),
    onBackground = Color(0xFFFFFFFF).copy(alpha = 0.87f),
    surface = Color(0xFF1C1E1C),
    onSurface = Color(0xFFFFFFFF).copy(alpha = 0.87f),
    surfaceVariant = Color(0xFF2A2D2A),
    onSurfaceVariant = Color(0xFFFFFFFF).copy(alpha = 0.7f),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF0A1014),
    outline = Color(0xFF4CAF50).copy(alpha = 0.5f)
)

enum class AppThemeMode {
    SYSTEM, LIGHT, DARK
}

enum class ThemePalette {
    NATURE, OCEAN, SUNSET
}

@Composable
fun AppTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    themePalette: ThemePalette = ThemePalette.NATURE,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val colorScheme = when (themePalette) {
        ThemePalette.OCEAN -> if (isDark) OceanDarkColors else OceanLightColors
        ThemePalette.NATURE -> if (isDark) NatureDarkColors else NatureLightColors
        ThemePalette.SUNSET -> if (isDark) NatureDarkColors else NatureLightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PoppinsTypography(),
        content = content
    )
}
