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
    override val main: Color = AppColorPalette.Green40
    override val mainContent: Color = AppColorPalette.White
    override val background: Color = AppColorPalette.Gray99
    override val text: Color = AppColorPalette.Gray10

    override fun mapToMaterialColorSchema(): ColorScheme = lightColorScheme(
        primary = main,
        onPrimary = mainContent,
        background = background,
        onBackground = text
    )
}

object AppDarkColorScheme : AppColorScheme {
    override val main: Color = AppColorPalette.Green80
    override val mainContent: Color = AppColorPalette.Green20
    override val background: Color = AppColorPalette.Gray10
    override val text: Color = AppColorPalette.Gray90

    override fun mapToMaterialColorSchema(): ColorScheme = darkColorScheme(
        primary = AppLightColorScheme.main,
        onPrimary = AppLightColorScheme.mainContent,
        background = AppLightColorScheme.background,
        onBackground = AppLightColorScheme.text
    )
}
