package com.iti.fineweather.core.helpers

sealed class Resource<T>(open val data: T? = null, open val error: Exception? = null){
    sealed class Success<T>(override val data: T): Resource<T>(data) {
        abstract fun <R> mapData(mapper: (T) -> R): Success<R>
    }

    class LocalSuccess<T>(override val data: T, val remoteError: Exception? = null): Success<T>(data) {
        override fun <R> mapData(mapper: (T) -> R): Success<R> = LocalSuccess(mapper(data), remoteError)
    }

    class RemoteSuccess<T>(override val data: T, val localError: Exception? = null): Success<T>(data) {
        override fun <R> mapData(mapper: (T) -> R): Success<R> = RemoteSuccess(mapper(data), localError)
    }

    class Error<T>(override val error: Exception, data:T? = null): Resource<T>(data = data, error = error)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Resource<*>

        if (data != other.data) return false
        if (error != other.error) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data?.hashCode() ?: 0
        result = 31 * result + (error?.hashCode() ?: 0)
        return result
    }
}
