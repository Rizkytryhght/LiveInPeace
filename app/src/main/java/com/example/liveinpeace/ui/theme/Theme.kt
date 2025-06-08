package com.example.liveinpeace.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = GreenPrimary,
    secondary = GreenLight,
    background = Background,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = TextGray,
    onSurface = Color.Black,
)

@Composable
fun LiveInPeaceTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}