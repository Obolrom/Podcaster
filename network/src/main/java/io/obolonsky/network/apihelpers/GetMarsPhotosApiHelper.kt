package io.obolonsky.network.apihelpers

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.network.api.MarsPhotosApi
import io.obolonsky.network.mappers.MarsPhotoRoverResponseToImageUrlsMapper
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.rx3.await
import java.lang.Exception
import javax.inject.Inject

class GetMarsPhotosApiHelper @Inject constructor(
    private val marsPhotoApi: MarsPhotosApi,
) : ApiHelperWith3Params<List<String>, Error, String, String, Int> {

    override suspend fun load(
        param1: String,
        param2: String,
        param3: Int
    ): Reaction<List<String>, Error> = try {
        val marsImages = marsPhotoApi.getPhotosByRover(param1, param2, param3)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .map { MarsPhotoRoverResponseToImageUrlsMapper.map(it) }
            .await()
            .orEmpty()

        Reaction.Success(marsImages)
    } catch (e: Exception) {
        Reaction.Fail(Error.NetworkError(e))
    }
}