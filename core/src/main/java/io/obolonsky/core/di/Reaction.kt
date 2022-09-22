package io.obolonsky.core.di

sealed class Reaction<out D, out E> {

    class Success<D>(val data: D) : Reaction<D, Nothing>()

    class Fail(val error: Error) : Reaction<Nothing, Error>()
}

inline fun <T> Reaction<T, Error>.reactWith(
    onSuccess: (T) -> Unit,
    onError: (Error) -> Unit,
) = when (this) {
    is Reaction.Success -> onSuccess(data)

    is Reaction.Fail -> onError(error)
}

fun <T> Reaction<T, Error>.reactWithSuccessOrDefault(default: T) = when (this) {
    is Reaction.Success -> data

    is Reaction.Fail -> default
}

fun <T> Reaction<T, Error>.reactWithSuccessOrNull() = reactWithSuccessOrDefault(null)