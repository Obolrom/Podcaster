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
) : ApiHelper<List<String>, GetMarsPhotosQueryParams> {

    override suspend fun load(
        param: GetMarsPhotosQueryParams
    ): Reaction<List<String>, Error> = try {
        val marsImages = marsPhotoApi.getPhotosByRover(
            name = param.roverName,
            earthDate = param.earthDate,
            page = param.page,
        )
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

data class GetMarsPhotosQueryParams(
    val roverName: String,
    val earthDate: String,
    val page: Int,
)