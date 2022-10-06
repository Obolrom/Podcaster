package io.obolonsky.core.di

sealed interface Reaction<out D> {

    interface Success<D>: Reaction<D> {

        val data: D
    }

    interface Fail: Reaction<Nothing> {

        val error: Error
    }

    companion object {

        fun <D> success(data: D): Success<D> = SuccessImpl(data)

        fun fail(error: Error): Fail = FailImpl(error)
    }
}

internal data class SuccessImpl<D>(override val data: D) : Reaction.Success<D>

internal data class FailImpl(override val error: Error) : Reaction.Fail


inline fun <T> Reaction<T>.reactWith(
    onSuccess: (T) -> Unit,
    onError: (Error) -> Unit,
) = when (this) {
    is Reaction.Success -> onSuccess(data)

    is Reaction.Fail -> onError(error)
}

fun <T> Reaction<T>.reactWithSuccessOrDefault(defaultProvider: () -> T) = when (this) {
    is Reaction.Success -> data

    is Reaction.Fail -> defaultProvider()
}

fun <T> Reaction<T>.reactWithSuccessOrNull() = reactWithSuccessOrDefault { null }