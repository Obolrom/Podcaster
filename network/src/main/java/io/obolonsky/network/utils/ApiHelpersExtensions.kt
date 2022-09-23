package io.obolonsky.network.utils

import com.haroldadmin.cnradapter.NetworkResponse
import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import retrofit2.HttpException

internal inline fun <R> runWithReaction(successBody: () -> R): Reaction<R, Error> {
    return try {
        Reaction.Success(successBody())
    } catch (httpError: HttpException) {
        Reaction.Fail(Error.NetworkError(httpError))
    } catch (e: Exception) {
        Reaction.Fail(Error.UnknownError(e))
    }
}

internal inline fun <I, O> NetworkResponse<I, *>.runWithReaction(
    successBody: I.() -> O
): Reaction<O, Error> = when (this) {
    is NetworkResponse.Success -> Reaction.Success(successBody(body))

    is NetworkResponse.Error -> Reaction.Fail(Error.UnknownError(error))
}