package io.obolonsky.core.di

sealed class Error {

    class NetworkError(val throwable: Throwable? = null) : Error()

    class UnknownError(val throwable: Throwable? = null, val message: String? = null) : Error()
}
