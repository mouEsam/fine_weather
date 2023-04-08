package com.iti.fineweather.features.common.utils

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.core.text.layoutDirection
import java.util.*


@Stable
fun Brush.Companion.horizontalGradientDirectional(
    colors: List<Color>,
    startX: Float = 0.0f,
    endX: Float = Float.POSITIVE_INFINITY,
): Brush {
    var colors = colors
    if (Locale.getDefault().layoutDirection == android.util.LayoutDirection.RTL) {
        colors = colors.reversed()
    }
    return horizontalGradient(
        colors = colors,
        startX = startX,
        endX = endX,
    )
}
