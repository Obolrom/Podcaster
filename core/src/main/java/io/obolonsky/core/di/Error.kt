package io.obolonsky.core.di

sealed class Error {

    class NetworkError(val throwable: Throwable? = null) : Error()
}
