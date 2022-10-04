package io.obolonsky.core.di

sealed interface Reaction<out D, out E> {

    interface Success<D>: Reaction<D, Nothing> {

        val data: D
    }

    interface Fail : Reaction<Nothing, Error> {

        val error: Error
    }

    companion object {

        fun <D> success(data: D): Success<D> = SuccessImpl(data)

        fun fail(error: Error): Fail = FailImpl(error)
    }
}

internal data class SuccessImpl<D>(override val data: D) : Reaction.Success<D>

internal data class FailImpl(override val error: Error) : Reaction.Fail


inline fun <T> Reaction<T, Error>.reactWith(
    onSuccess: (T) -> Unit,
    onError: (Error) -> Unit,
) = when (this) {
    is Reaction.Success -> onSuccess(data)

    is Reaction.Fail -> onError(error)
}

fun <T> Reaction<T, Error>.reactWithSuccessOrDefault(defaultProvider: () -> T) = when (this) {
    is Reaction.Success -> data

    is Reaction.Fail -> defaultProvider()
}

fun <T> Reaction<T, Error>.reactWithSuccessOrNull() = reactWithSuccessOrDefault { null }