package io.obolonsky.network.apihelpers.base

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.api.Query
import com.apollographql.apollo3.exception.ApolloHttpException
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.apollographql.apollo3.exception.JsonDataException
import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.core.di.utils.Mapper
import kotlinx.coroutines.flow.*

abstract class BaseSingleFlowGraphQlApiHelper<ApiResult : Query.Data, DomainResult, ApiParam>(
    private val dispatchers: CoroutineSchedulers,
    private val mapper: Mapper<ApiResult, DomainResult>
) : SingleFlowApiHelper<ApiParam, DomainResult> {

    abstract fun apiRequest(param: ApiParam): ApolloCall<ApiResult>

    override fun load(param: ApiParam): Flow<Reaction<DomainResult>> {
        return apiRequest(param)
            .toFlow()
            .map { mapper.map(it.dataAssertNoErrors) }
            .map<DomainResult, Reaction<DomainResult>> { Reaction.success(it) }
            .catch { emit(it.apolloWithReaction()) }
            .flowOn(dispatchers.computation)
    }
}

private fun Throwable.apolloWithReaction(): Reaction.Fail = when (this) {
    is ApolloNetworkException -> Reaction.fail(Error.NetworkError(this))

    is JsonDataException -> Reaction.fail(Error.ServerError(this))

    is ApolloHttpException -> Reaction.fail(Error.ServerError(this))

    else -> Reaction.fail(Error.UnknownError(this))
}