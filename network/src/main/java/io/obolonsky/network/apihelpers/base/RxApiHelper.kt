package io.obolonsky.network.apihelpers.base

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.utils.RxSchedulers
import io.obolonsky.network.utils.runWithReaction
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.rx3.await

abstract class RxApiHelper<ApiResult : Any, DomainResult, ApiParam>(
    private val rxSchedulers: RxSchedulers,
    private val mapper: Mapper<ApiResult, DomainResult>
) : ApiHelper<DomainResult, ApiParam> {

    abstract fun apiRequest(param: ApiParam): Single<ApiResult>

    abstract fun DomainResult?.onNullableReturn(): DomainResult

    final override suspend fun load(param: ApiParam): Reaction<DomainResult, Error> = runWithReaction {
        apiRequest(param)
            .subscribeOn(rxSchedulers.io)
            .observeOn(rxSchedulers.computation)
            .map(mapper::map)
            .await()
            .onNullableReturn()
    }
}