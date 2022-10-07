package io.obolonsky.core.di.utils

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import kotlinx.coroutines.flow.*

inline fun <T> Flow<Reaction<T>>.reactWith(
    crossinline onSuccess: suspend (T) -> Unit,
    crossinline onError: suspend (Error) -> Unit,
) = onEach { reaction ->
    when (reaction) {
        is Reaction.Success -> onSuccess(reaction.data)

        is Reaction.Fail -> onError(reaction.error)
    }
}