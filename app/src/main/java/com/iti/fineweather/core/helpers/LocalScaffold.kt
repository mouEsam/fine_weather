package com.iti.fineweather.core.helpers

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*

object LocalScaffold {
    val snackbarHost: SnackbarHostState
        @Composable
        @ReadOnlyComposable
        get() = LocalScaffoldState.current
}

private val LocalScaffoldState: ProvidableCompositionLocal<SnackbarHostState> = compositionLocalOf { error("not provided") }

@Composable
fun CompositionScaffoldProvider(
    snackbarHost: SnackbarHostState = remember { SnackbarHostState() },
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalScaffoldState provides snackbarHost,
        content = content,
    )
}
