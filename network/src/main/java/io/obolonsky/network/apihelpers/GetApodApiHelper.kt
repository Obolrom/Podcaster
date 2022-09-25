package io.obolonsky.network.apihelpers

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.network.api.NasaApodApi
import io.obolonsky.network.mappers.ApodResponseToImageUrlsMapper
import io.obolonsky.network.utils.RxSchedulers
import io.obolonsky.network.utils.runWithReaction
import kotlinx.coroutines.rx3.await
import javax.inject.Inject

class GetApodApiHelper @Inject constructor(
    private val apodApi: NasaApodApi,
    private val rxSchedulers: RxSchedulers,
) : ApiHelper<List<String>, Int> {

    override suspend fun load(param: Int): Reaction<List<String>, Error> = runWithReaction {
        apodApi.getRandomApod(param)
            .subscribeOn(rxSchedulers.io)
            .observeOn(rxSchedulers.computation)
            .map(ApodResponseToImageUrlsMapper::map)
            .await()
            .orEmpty()
    }
}