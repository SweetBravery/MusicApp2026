package com.example.musicapp2026.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

enum class ThemeType {
    CLASSIC_GOLD, OCEAN_BREEZE, SAKURA, FOREST_LIGHT, ROYAL_PURPLE,
    DEEP_MIDNIGHT, NEON_CYBER, NORDIC_ICE, BLOOD_MOON, OBSIDIAN_MINT
}

object AppThemePresets {
    // Light Themes
    val ClassicGoldLight = lightColorScheme(
        primary = Color(0xFFFFD700),
        onPrimary = Color.Black,
        surface = Color(0xFFFFFBFE),
        background = Color(0xFFFFFBFE)
    )

    val OceanBreezeLight = lightColorScheme(
        primary = Color(0xFF008080),
        onPrimary = Color.White,
        surface = Color(0xFFF0F8FF),
        background = Color(0xFFF0F8FF)
    )

    val SakuraLight = lightColorScheme(
        primary = Color(0xFFFFB7C5),
        onPrimary = Color.White,
        surface = Color(0xFFFFF5F7),
        background = Color(0xFFFFF5F7)
    )

    val ForestLight = lightColorScheme(
        primary = Color(0xFF2E8B57),
        onPrimary = Color.White,
        surface = Color(0xFFF5FFFA),
        background = Color(0xFFF5FFFA)
    )

    val RoyalPurpleLight = lightColorScheme(
        primary = Color(0xFF7851A9),
        onPrimary = Color.White,
        surface = Color(0xFFF9F5FF),
        background = Color(0xFFF9F5FF)
    )

    // Dark Themes
    val DeepMidnightDark = darkColorScheme(
        primary = Color(0xFFFFD700),
        onPrimary = Color.Black,
        surface = Color(0xFF121212),
        background = Color(0xFF121212)
    )

    val NeonCyberDark = darkColorScheme(
        primary = Color(0xFFFF00FF),
        onPrimary = Color.Black,
        surface = Color(0xFF0A0A12),
        background = Color(0xFF0A0A12)
    )

    val NordicIceDark = darkColorScheme(
        primary = Color(0xFF88C0D0),
        onPrimary = Color(0xFF2E3440),
        surface = Color(0xFF2E3440),
        background = Color(0xFF2E3440)
    )

    val BloodMoonDark = darkColorScheme(
        primary = Color(0xFFDC143C),
        onPrimary = Color.White,
        surface = Color(0xFF1A0A0A),
        background = Color(0xFF1A0A0A)
    )

    val ObsidianMintDark = darkColorScheme(
        primary = Color(0xFF32CD32),
        onPrimary = Color.Black,
        surface = Color(0xFF050505),
        background = Color(0xFF050505)
    )

    fun getColorScheme(type: ThemeType): androidx.compose.material3.ColorScheme {
        return when (type) {
            ThemeType.CLASSIC_GOLD -> ClassicGoldLight
            ThemeType.OCEAN_BREEZE -> OceanBreezeLight
            ThemeType.SAKURA -> SakuraLight
            ThemeType.FOREST_LIGHT -> ForestLight
            ThemeType.ROYAL_PURPLE -> RoyalPurpleLight
            ThemeType.DEEP_MIDNIGHT -> DeepMidnightDark
            ThemeType.NEON_CYBER -> NeonCyberDark
            ThemeType.NORDIC_ICE -> NordicIceDark
            ThemeType.BLOOD_MOON -> BloodMoonDark
            ThemeType.OBSIDIAN_MINT -> ObsidianMintDark
        }
    }
}
