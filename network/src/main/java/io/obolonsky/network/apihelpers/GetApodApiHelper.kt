package io.obolonsky.network.apihelpers

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.network.api.NasaApodApi
import io.obolonsky.network.mappers.ApodResponseToImageUrlsMapper
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.rx3.await
import javax.inject.Inject

class GetApodApiHelper @Inject constructor(
    private val apodApi: NasaApodApi,
) : ApiHelperWithOneParam<List<String>, Error, Int> {

    override suspend fun load(param: Int): Reaction<List<String>, Error> = try {
        val apodImages = apodApi.getRandomApod(param)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .map { ApodResponseToImageUrlsMapper.map(it) }
            .await()
            .orEmpty()

        Reaction.Success(apodImages)
    } catch (e: Exception) {
        Reaction.Fail(Error.NetworkError(e))
    }
}