package io.obolonsky.network.apihelpers.base

import com.haroldadmin.cnradapter.NetworkResponse
import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.utils.runWithReaction
import kotlinx.coroutines.withContext

abstract class CoroutinesApiHelper<ApiResult, DomainResult, ApiParam>(
    private val dispatchers: CoroutineSchedulers,
    private val mapper: Mapper<ApiResult, DomainResult>
) : ApiHelper<DomainResult, ApiParam> {

    /**
     * Already executes on [CoroutineSchedulers.computation]
     */
    abstract suspend fun apiRequest(param: ApiParam): NetworkResponse<ApiResult, *>

    override suspend fun load(param: ApiParam): Reaction<DomainResult, Error> {
        val apiResult = withContext(dispatchers.io) {
            apiRequest(param)
        }
        return apiResult.runWithReaction {
            withContext(dispatchers.computation) {
                mapper.map(this@runWithReaction)
            }
        }
    }
}