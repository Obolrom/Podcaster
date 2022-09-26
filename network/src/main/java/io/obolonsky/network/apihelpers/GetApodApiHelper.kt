package io.obolonsky.network.apihelpers

import io.obolonsky.network.api.NasaApodApi
import io.obolonsky.network.apihelpers.base.RxApiHelper
import io.obolonsky.network.mappers.ApodResponseToImageUrlsMapper
import io.obolonsky.network.responses.nasa.ApodResponse
import io.obolonsky.network.utils.RxSchedulers
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetApodApiHelper @Inject constructor(
    private val apodApi: NasaApodApi,
    rxSchedulers: RxSchedulers,
) : RxApiHelper<List<ApodResponse>, List<String>, Int>(
    rxSchedulers = rxSchedulers,
    mapper = ApodResponseToImageUrlsMapper
) {

    override fun apiRequest(param: Int): Single<List<ApodResponse>> {
        return apodApi.getRandomApod(param)
    }

    override fun List<String>?.onNullableReturn(): List<String> = orEmpty()
}