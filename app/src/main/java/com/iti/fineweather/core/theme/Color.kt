package com.iti.fineweather.core.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

interface AppColorScheme {
    val main: Color
    val mainContent: Color
    val background: Color
    val text: Color

    fun mapToMaterialColorSchema(): ColorScheme
}

object AppLightColorScheme : AppColorScheme {
    override val main: Color = AppColorPalette.Blue40
    override val mainContent: Color = AppColorPalette.White
    override val background: Color = AppColorPalette.Gray99
    override val text: Color = AppColorPalette.Gray10

    override fun mapToMaterialColorSchema(): ColorScheme = lightColorScheme(
        primary = main,
        onPrimary = mainContent,
        background = background,
        onBackground = text,
        surface = background,
        onSurface = text,
    )
}

object AppDarkColorScheme : AppColorScheme {
    override val main: Color = AppColorPalette.Indigo40
    override val mainContent: Color = AppColorPalette.White
    override val background: Color = AppColorPalette.Purple40
    override val text: Color = AppColorPalette.White

    override fun mapToMaterialColorSchema(): ColorScheme = darkColorScheme(
        primary = main,
        onPrimary = mainContent,
        background = background,
        onBackground = text,
        surface = mainContent,
        onSurface = AppColorPalette.Blue40,
        surfaceVariant = AppColorPalette.Gray10,
    )
}
