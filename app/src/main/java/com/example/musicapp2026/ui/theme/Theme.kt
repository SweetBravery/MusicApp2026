package com.example.musicapp2026.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun MusicAppTheme(
    themeType: ThemeType = ThemeType.DEEP_MIDNIGHT,
    content: @Composable () -> Unit
) {
    val colorScheme = AppThemePresets.getColorScheme(themeType)

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
