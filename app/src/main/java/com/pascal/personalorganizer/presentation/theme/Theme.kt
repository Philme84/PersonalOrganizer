package com.pascal.personalorganizer.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.pascal.personalorganizer.ui.theme.*

private val DarkColorPalette = darkColors(
    primary = bluePrimary,
    secondary = redSecondary,
    onPrimary = Color.White,
    primaryVariant = Purple700,
    surface = softBlack,
    onSurface = Color.White,
    background = backgroundGrey,
    onBackground = Color.White
)

private val LightColorPalette = lightColors(
    primary = bluePrimary,
    onPrimary = Color.White,
    primaryVariant = Purple700,
    secondary = redSecondary,
    surface = softBlack,
    onSurface = Color.White,
    background = backgroundGrey,
    onBackground = Color.White
)

@Composable
fun PersonalOrganizerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}