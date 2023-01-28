package io.obolonsky.network.apihelpers.base

import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.utils.RxSchedulers
import io.obolonsky.network.utils.runWithReaction
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.rx3.await
import timber.log.Timber

abstract class RxApiHelper<ApiResult : Any, DomainResult, ApiParam>(
    private val rxSchedulers: RxSchedulers,
    private val mapper: Mapper<ApiResult, DomainResult>
) : ApiHelper<DomainResult, ApiParam> {

    abstract fun apiRequest(param: ApiParam): Single<ApiResult>

    open fun DomainResult?.onNullableReturn(): DomainResult {
        error("Should not be null")
    }

    final override suspend fun load(
        param: ApiParam
    ): Reaction<DomainResult> = runWithReaction {
        apiRequest(param)
            .doOnEvent { t1, t2 ->
                Timber.d("fuckingFuck onEvent t1 $t1, t2 $t2")
            }
            .subscribeOn(rxSchedulers.io)
            .observeOn(rxSchedulers.computation)
            .map(mapper::map)
            .await()
            .let { result ->
                result ?: result.onNullableReturn()
            }
    }
}