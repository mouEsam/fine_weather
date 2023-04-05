package com.iti.fineweather.features.common.views

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

@Composable
fun ClearStatusBar(
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    val statusBarColor = rememberSaveable {
        (view.context as Activity).window.statusBarColor
    }
    SideEffect {
        val window = (view.context as Activity).window
        window.statusBarColor = Color.Transparent.toArgb()
    }
    DisposableEffect(key1 = statusBarColor) {
        onDispose {
            (view.context as Activity).window.statusBarColor = statusBarColor
        }
    }
    content()
}