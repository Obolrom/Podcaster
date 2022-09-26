package io.obolonsky.network.apihelpers.base

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.api.Query
import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.utils.runWithReaction
import kotlinx.coroutines.withContext

abstract class GraphQlApiHelper<ApiResult : Query.Data, DomainResult, ApiParam>(
    private val dispatchers: CoroutineSchedulers,
    private val mapper: Mapper<ApiResult, DomainResult>
) : ApiHelper<DomainResult, ApiParam> {

    abstract fun apiRequest(param: ApiParam): ApolloCall<ApiResult>

    final override suspend fun load(
        param: ApiParam
    ): Reaction<DomainResult, Error> = runWithReaction {
        val apiResult = withContext(dispatchers.io) {
            apiRequest(param)
                .execute()
                .dataAssertNoErrors
        }

        withContext(dispatchers.computation) {
            mapper.map(apiResult)
        }
    }
}