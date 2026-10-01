package com.sonusstudios.sonusedit.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object SonusEditColors {
    val Black = Color(0xFF080A0F)
    val Surface = Color(0xFF10131B)
    val SurfaceRaised = Color(0xFF171B25)
    val Violet = Color(0xFF8B5CF6)
    val VioletBright = Color(0xFFA78BFA)
    val Cyan = Color(0xFF22D3EE)
    val Text = Color(0xFFF8FAFC)
    val TextMuted = Color(0xFF94A3B8)
    val Danger = Color(0xFFF43F5E)
}

private val SonusEditScheme = darkColorScheme(
    primary = SonusEditColors.Violet,
    secondary = SonusEditColors.Cyan,
    background = SonusEditColors.Black,
    surface = SonusEditColors.Surface,
    surfaceVariant = SonusEditColors.SurfaceRaised,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = SonusEditColors.Text,
    onSurface = SonusEditColors.Text,
    error = SonusEditColors.Danger,
)

@Composable
fun SonusEditTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SonusEditScheme,
        content = content,
    )
}
