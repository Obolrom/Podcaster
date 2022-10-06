package io.obolonsky.core.di.repositories

import io.obolonsky.core.di.Reaction

interface NasaRepo {

    suspend fun getApodUrls(imagesRequestCount: Int): Reaction<List<String>>

    suspend fun getMarsImageUrls(
        roverName: String,
        earthDate: String,
        page: Int
    ): Reaction<List<String>>
}