package io.obolonsky.core.di

sealed class Error {

    data class NetworkError(val throwable: Throwable? = null) : Error()

    data class ServerError(val throwable: Throwable? = null) : Error()

    data class SerializationError(val throwable: Throwable? = null) : Error()

    data class UnknownError(val throwable: Throwable? = null, val message: String? = null) : Error()
}
