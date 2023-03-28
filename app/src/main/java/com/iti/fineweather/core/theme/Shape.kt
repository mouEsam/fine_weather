package com.iti.fineweather.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

object AppShape {
    val smallRoundedCornerShape: RoundedCornerShape
        @Composable
        get() = RoundedCornerShape(4.dp)

    val mediumRoundedCornerShape: RoundedCornerShape
        @Composable
        get() = RoundedCornerShape(8.dp)

    val largeRoundedCornerShape: RoundedCornerShape
        @Composable
        get() = RoundedCornerShape(12.dp)
}