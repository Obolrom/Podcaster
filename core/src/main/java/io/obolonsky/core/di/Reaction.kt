package io.obolonsky.core.di

sealed class Reaction<out D, out E> {

    class Success<D>(val data: D) : Reaction<D, Nothing>()

    class Fail(val error: Error) : Reaction<Nothing, Error>()
}