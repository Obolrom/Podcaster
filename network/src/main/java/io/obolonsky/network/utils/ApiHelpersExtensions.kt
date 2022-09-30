package io.obolonsky.network.utils

import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.google.gson.JsonParseException
import com.haroldadmin.cnradapter.NetworkResponse
import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import retrofit2.HttpException
import timber.log.Timber

internal inline fun <R> runWithReaction(successBody: () -> R): Reaction<R, Error> {
    return try {
        Reaction.Success(successBody())
    } catch (httpError: HttpException) {
        Reaction.Fail(Error.NetworkError(httpError))
    } catch (apolloException: ApolloException) {
        Timber.e(apolloException)
        when (apolloException) {
            is ApolloNetworkException -> Reaction.Fail(Error.NetworkError(apolloException))

            else -> Reaction.Fail(Error.UnknownError(apolloException))
        }
        Reaction.Fail(Error.NetworkError(apolloException))
    } catch (e: Exception) {
        Reaction.Fail(Error.UnknownError(e))
    }
}

internal inline fun <I, O> NetworkResponse<I, *>.runWithReaction(
    successBody: I.() -> O
): Reaction<O, Error> = when (this) {
    is NetworkResponse.Success -> Reaction.Success(successBody(body))

    is NetworkResponse.ServerError -> Reaction.Fail(Error.ServerError(error))

    is NetworkResponse.NetworkError -> Reaction.Fail(Error.NetworkError(error))

    is NetworkResponse.Error -> {
        val domainError = when (error) {
            is JsonParseException -> Error.SerializationError(error)

            else -> Error.UnknownError(error)
        }
        Reaction.Fail(domainError)
    }
}