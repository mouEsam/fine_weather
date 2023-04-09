package com.iti.fineweather.core.utils

import com.iti.fineweather.core.helpers.Resource
import com.iti.fineweather.core.helpers.UiState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import timber.log.Timber

suspend fun <T> MutableSharedFlow<UiState<T>>.wrapResource(operation: suspend () -> Resource<out T>) {
    emit(UiState.Loading())
    val result = operation()
    when (result) {
        is Resource.Success -> UiState.Loaded(result.data)
        is Resource.Error -> UiState.Error(result.error)
    }.let { state -> emit(state) }
}

suspend fun <T> MutableSharedFlow<UiState<T>>.wrap(operation: suspend () -> T): UiState<out T> {
    emit(UiState.Loading())
    return try {
        coroutineScope {
            UiState.Loaded(operation())
        }
    } catch (e: Exception) {
        Timber.e(e)
        UiState.Error(e)
    }.also { it -> emit(it) }
}

suspend fun <T> (suspend () -> T).wrap() = channelFlow {
    send(UiState.Loading())
    try {
        coroutineScope {
            send(UiState.Loaded(invoke()))
        }
    } catch (e: Exception) {
        Timber.e(e)
        send(UiState.Error(e))
    }
    close()
}
