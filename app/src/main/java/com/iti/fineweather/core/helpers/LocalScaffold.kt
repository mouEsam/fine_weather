package com.iti.fineweather.core.helpers

import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*

object LocalScaffold {
    val current: ScaffoldState
        @Composable
        @ReadOnlyComposable
        get() = LocalScaffoldState.current
}

private val LocalScaffoldState: ProvidableCompositionLocal<ScaffoldState> = compositionLocalOf { error("not provided") }

@Composable
fun CompositionScaffoldProvider(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalScaffoldState provides scaffoldState,
        content = content,
    )
}
