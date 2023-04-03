package com.iti.fineweather.core.helpers

sealed class UiState<T>(open val data: T? = null, open val error: Exception? = null) {
    data class Initial<T>(private val dummy: Nothing? = null): UiState<T>()
    data class Loaded<T>(override val data: T): UiState<T>(data)
    data class Error<T>(override val error: Exception, override val data:T? = null): UiState<T>(data = data, error = error)
    data class Loading<T>(override val data: T? = null): UiState<T>(data = data)

    fun toLoading(preserveData: Boolean = true): UiState<T> = Loading(if (preserveData) data else null)
    fun toError(error: Exception, preserveData: Boolean = true): UiState<T> = Error(error, if (preserveData) data else null)
    fun toLoaded(data: T): UiState<T> = Loaded(data)
}
