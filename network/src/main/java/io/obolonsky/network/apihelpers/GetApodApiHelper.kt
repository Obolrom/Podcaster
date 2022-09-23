package io.obolonsky.network.apihelpers

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.network.api.NasaApodApi
import io.obolonsky.network.mappers.ApodResponseToImageUrlsMapper
import io.obolonsky.network.utils.RxSchedulers
import kotlinx.coroutines.rx3.await
import retrofit2.HttpException
import javax.inject.Inject

class GetApodApiHelper @Inject constructor(
    private val apodApi: NasaApodApi,
    private val rxSchedulers: RxSchedulers,
) : ApiHelper<List<String>, Int> {

    override suspend fun load(param: Int): Reaction<List<String>, Error> = try {
        val apodImages = apodApi.getRandomApod(param)
            .subscribeOn(rxSchedulers.io)
            .observeOn(rxSchedulers.computation)
            .map(ApodResponseToImageUrlsMapper::map)
            .await()
            .orEmpty()

        Reaction.Success(apodImages)
    } catch (httpError: HttpException) {
        Reaction.Fail(Error.NetworkError(httpError))
    } catch (e: Exception) {
        Reaction.Fail(Error.UnknownError(e))
    }
}