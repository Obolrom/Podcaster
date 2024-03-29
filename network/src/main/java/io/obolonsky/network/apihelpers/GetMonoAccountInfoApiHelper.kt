package io.obolonsky.network.apihelpers

import io.obolonsky.core.di.Reaction
import io.obolonsky.network.api.MonoBankApi
import io.obolonsky.network.apihelpers.base.ApiHelper
import io.obolonsky.network.utils.RxSchedulers
import io.obolonsky.network.utils.runWithReaction
import kotlinx.coroutines.rx3.await
import javax.inject.Inject

class GetMonoAccountInfoApiHelper @Inject constructor(
    private val monoBankApi: MonoBankApi,
    private val rxSchedulers: RxSchedulers,
) : ApiHelper<Unit, Unit> {

    override suspend fun load(param: Unit): Reaction<Unit> = runWithReaction {
        monoBankApi.getAccountInfo()
            .subscribeOn(rxSchedulers.io)
            .observeOn(rxSchedulers.computation)
            .map {  }
            .await()
    }
}