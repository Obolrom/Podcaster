package io.obolonsky.network.apihelpers

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.network.api.MarsPhotosApi
import io.obolonsky.network.mappers.MarsPhotoRoverResponseToImageUrlsMapper
import io.obolonsky.network.utils.RxSchedulers
import kotlinx.coroutines.rx3.await
import java.lang.Exception
import javax.inject.Inject

class GetMarsPhotosApiHelper @Inject constructor(
    private val marsPhotoApi: MarsPhotosApi,
    private val rxSchedulers: RxSchedulers,
) : ApiHelper<List<String>, GetMarsPhotosApiHelper.QueryParams> {

    override suspend fun load(
        param: QueryParams
    ): Reaction<List<String>, Error> = try {
        val marsImages = marsPhotoApi.getPhotosByRover(
            name = param.roverName,
            earthDate = param.earthDate,
            page = param.page,
        )
            .subscribeOn(rxSchedulers.io)
            .observeOn(rxSchedulers.computation)
            .map(MarsPhotoRoverResponseToImageUrlsMapper::map)
            .await()
            .orEmpty()

        Reaction.Success(marsImages)
    } catch (e: Exception) {
        Reaction.Fail(Error.NetworkError(e))
    }

    data class QueryParams(
        val roverName: String,
        val earthDate: String,
        val page: Int,
    )
}