package com.example.amilimetros.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DeepPurple,
    primaryContainer = BluePurple,
    onPrimaryContainer = ElectricBlue,

    secondary = DarkSecondary,
    onSecondary = RichPurple,
    secondaryContainer = CornflowerBlue,
    onSecondaryContainer = Aquamarine,

    tertiary = DarkTertiary,
    onTertiary = DeepPurple,
    tertiaryContainer = BlueJeans,
    onTertiaryContainer = ElectricBlue,

    background = DeepPurple,
    onBackground = ElectricBlue,
    surface = RichPurple,
    onSurface = Aquamarine,
    surfaceVariant = BluePurple,
    onSurfaceVariant = Turquoise
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = ElectricBlue,
    primaryContainer = SkyBlue,
    onPrimaryContainer = DeepPurple,

    secondary = LightSecondary,
    onSecondary = Aquamarine,
    secondaryContainer = Turquoise,
    onSecondaryContainer = RichPurple,

    tertiary = LightTertiary,
    onTertiary = ElectricBlue,
    tertiaryContainer = MediumCyan,
    onTertiaryContainer = DeepPurple,

    background = ElectricBlue,
    onBackground = DeepPurple,
    surface = Aquamarine,
    onSurface = DeepPurple,
    surfaceVariant = Turquoise,
    onSurfaceVariant = RichPurple
)

@Composable
fun AMilimetrosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // ✅ Desactivado para usar nuestra paleta
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}