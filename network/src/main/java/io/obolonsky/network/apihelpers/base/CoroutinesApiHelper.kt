package io.obolonsky.network.apihelpers.base

import com.haroldadmin.cnradapter.NetworkResponse
import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.utils.runWithReaction
import kotlinx.coroutines.flow.*

abstract class CoroutinesApiHelper<ApiResult, DomainResult, ApiParam>(
    private val dispatchers: CoroutineSchedulers,
    private val mapper: Mapper<ApiResult, DomainResult>
) : ApiHelper<DomainResult, ApiParam> {

    /**
     * Already executes on [CoroutineSchedulers.io]
     */
    abstract suspend fun apiRequest(param: ApiParam): NetworkResponse<ApiResult, *>

    final override suspend fun load(param: ApiParam): Reaction<DomainResult, Error> {
        return flowOf(apiRequest(param))
            .flowOn(dispatchers.io)
            .map { it.runWithReaction { mapper.map(this) } }
            .flowOn(dispatchers.computation)
            .catch { emit(Reaction.Fail(Error.UnknownError(it))) }
            .single()
    }
}