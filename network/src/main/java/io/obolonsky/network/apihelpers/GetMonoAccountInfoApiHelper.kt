package io.obolonsky.network.apihelpers

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.network.api.MonoBankApi
import io.obolonsky.network.utils.RxSchedulers
import kotlinx.coroutines.rx3.await
import javax.inject.Inject

class GetMonoAccountInfoApiHelper @Inject constructor(
    private val monoBankApi: MonoBankApi,
    private val rxSchedulers: RxSchedulers,
) : ApiHelper<Unit, Unit> {

    override suspend fun load(param: Unit): Reaction<Unit, Error> = try {
        val result = monoBankApi.getAccountInfo()
            .subscribeOn(rxSchedulers.io)
            .observeOn(rxSchedulers.computation)
            .map {  }
            .await()

        Reaction.Success(result)
    } catch (e: Exception) {
        Reaction.Fail(Error.NetworkError(e))
    }
}