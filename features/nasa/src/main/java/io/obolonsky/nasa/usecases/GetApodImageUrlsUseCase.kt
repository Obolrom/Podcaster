package io.obolonsky.nasa.usecases

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.nasa.di.ScopedNasaRepo
import javax.inject.Inject

internal class GetApodImageUrlsUseCase @Inject constructor(
    private val nasaRepo: ScopedNasaRepo,
) {

    suspend operator fun invoke(imagesRequestCount: Int): Reaction<List<String>, Error> {
        return nasaRepo.getApodUrls(imagesRequestCount)
    }
}