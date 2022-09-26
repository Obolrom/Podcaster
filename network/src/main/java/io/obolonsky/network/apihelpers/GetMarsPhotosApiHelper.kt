package io.obolonsky.network.apihelpers

import io.obolonsky.network.api.MarsPhotosApi
import io.obolonsky.network.apihelpers.base.RxApiHelper
import io.obolonsky.network.mappers.MarsPhotoRoverResponseToImageUrlsMapper
import io.obolonsky.network.responses.nasa.MarsPhotoRoverResponse
import io.obolonsky.network.utils.RxSchedulers
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetMarsPhotosApiHelper @Inject constructor(
    private val marsPhotoApi: MarsPhotosApi,
    rxSchedulers: RxSchedulers,
) : RxApiHelper<MarsPhotoRoverResponse, List<String>, GetMarsPhotosApiHelper.QueryParams>(
    rxSchedulers = rxSchedulers,
    mapper = MarsPhotoRoverResponseToImageUrlsMapper,
) {

    override fun apiRequest(param: QueryParams): Single<MarsPhotoRoverResponse> {
        return marsPhotoApi.getPhotosByRover(
            name = param.roverName,
            earthDate = param.earthDate,
            page = param.page,
        )
    }

    override fun List<String>?.onNullableReturn(): List<String> = orEmpty()

    data class QueryParams(
        val roverName: String,
        val earthDate: String,
        val page: Int,
    )
}