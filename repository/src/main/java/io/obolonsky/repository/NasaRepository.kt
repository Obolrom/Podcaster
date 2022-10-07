package io.obolonsky.repository

import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.repositories.NasaRepo
import io.obolonsky.network.apihelpers.GetApodApiHelper
import io.obolonsky.network.apihelpers.GetMarsPhotosApiHelper
import javax.inject.Inject

class NasaRepository @Inject constructor(
    private val apodApiHelper: GetApodApiHelper,
    private val marsPhotosApiHelper: GetMarsPhotosApiHelper,
) : NasaRepo {

    override suspend fun getApodUrls(imagesRequestCount: Int): Reaction<List<String>> {
        return apodApiHelper.load(imagesRequestCount)
    }

    override suspend fun getMarsImageUrls(
        roverName: String,
        earthDate: String,
        page: Int
    ): Reaction<List<String>> {
        return marsPhotosApiHelper.load(
            GetMarsPhotosApiHelper.QueryParams(
                roverName = roverName,
                earthDate = earthDate,
                page = page,
            )
        )
    }
}