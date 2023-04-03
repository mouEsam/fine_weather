package com.iti.fineweather.core.utils

import com.iti.fineweather.core.helpers.UiState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow

suspend fun <T> MutableSharedFlow<UiState<T>>.wrap(operation: suspend () -> T): UiState<T> {
    tryEmit(UiState.Loading())
    return try {
        coroutineScope {
            UiState.Loaded(operation())
        }
    } catch (e: Exception) {
        UiState.Error(e)
    }
}

suspend fun <T> (suspend () -> T).wrap() = channelFlow {
    trySend(UiState.Loading())
    try {
        coroutineScope {
            trySend(UiState.Loaded(invoke()))
        }
    } catch (e: Exception) {
        trySend(UiState.Error(e))
    }
    close()
}
