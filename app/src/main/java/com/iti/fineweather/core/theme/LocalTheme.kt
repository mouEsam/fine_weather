package com.iti.fineweather.core.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

object LocalTheme {
    val colors: AppColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColor.current
    val typography: AppTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalAppTypography.current
    val shapes: AppShape
        @Composable
        @ReadOnlyComposable
        get() = LocalAppShape.current
    val spaces: AppSpace
        @Composable
        @ReadOnlyComposable
        get() = LocalAppSpace.current
}

val LocalAppColor: ProvidableCompositionLocal<AppColorScheme> = staticCompositionLocalOf { error("not provided") }

val LocalAppTypography: ProvidableCompositionLocal<AppTypography> = staticCompositionLocalOf { error("not provided") }

val LocalAppShape: ProvidableCompositionLocal<AppShape> = staticCompositionLocalOf { error("not provided") }

val LocalAppSpace: ProvidableCompositionLocal<AppSpace> = staticCompositionLocalOf { error("not provided") }
