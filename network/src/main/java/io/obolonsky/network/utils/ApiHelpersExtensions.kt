package io.obolonsky.network.utils

import com.apollographql.apollo3.exception.*
import com.google.gson.JsonParseException
import com.haroldadmin.cnradapter.NetworkResponse
import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import retrofit2.HttpException
import timber.log.Timber

internal inline fun <R> runWithReaction(successBody: () -> R): Reaction<R> {
    return try {
        Reaction.success(successBody())
    } catch (httpError: HttpException) {
        Reaction.fail(Error.NetworkError(httpError))
    } catch (apolloException: ApolloException) {
        Timber.e(apolloException)
        return when (apolloException) {
            is ApolloNetworkException -> Reaction.fail(Error.NetworkError(apolloException))

            is JsonDataException -> Reaction.fail(Error.ServerError(apolloException))

            is ApolloHttpException -> Reaction.fail(Error.ServerError(apolloException))

            else -> Reaction.fail(Error.UnknownError(apolloException))
        }
    } catch (e: Exception) {
        Reaction.fail(Error.UnknownError(e))
    }
}

internal inline fun <I, O> NetworkResponse<I, *>.runWithReaction(
    successBody: I.() -> O
): Reaction<O> = when (this) {
    is NetworkResponse.Success -> Reaction.success(successBody(body))

    is NetworkResponse.ServerError -> Reaction.fail(Error.ServerError(error))

    is NetworkResponse.NetworkError -> Reaction.fail(Error.NetworkError(error))

    is NetworkResponse.Error -> {
        val domainError = when (error) {
            is JsonParseException -> Error.SerializationError(error)

            else -> Error.UnknownError(error)
        }
        Reaction.fail(domainError)
    }
}