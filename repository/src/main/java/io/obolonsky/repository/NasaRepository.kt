package io.obolonsky.repository

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.repositories.NasaRepo
import io.obolonsky.network.apihelpers.GetApodApiHelper
import io.obolonsky.network.apihelpers.GetMarsPhotosApiHelper
import io.obolonsky.network.apihelpers.GetMarsPhotosQueryParams
import javax.inject.Inject

class NasaRepository @Inject constructor(
    private val apodApiHelper: GetApodApiHelper,
    private val marsPhotosApiHelper: GetMarsPhotosApiHelper,
) : NasaRepo {

    override suspend fun getApodUrls(imagesRequestCount: Int): Reaction<List<String>, Error> {
        return apodApiHelper.load(imagesRequestCount)
    }

    override suspend fun getMarsImageUrls(
        roverName: String,
        earthDate: String,
        page: Int
    ): Reaction<List<String>, Error> {
        return marsPhotosApiHelper.load(
            GetMarsPhotosQueryParams(
                roverName = roverName,
                earthDate = earthDate,
                page = page,
            )
        )
    }
}