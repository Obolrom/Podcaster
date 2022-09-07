package io.obolonsky.network.mappers

import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.responses.nasa.MarsPhotoRoverResponse

object MarsPhotoRoverResponseToImageUrlsMapper : Mapper<MarsPhotoRoverResponse?, List<String>> {

    override fun map(input: MarsPhotoRoverResponse?): List<String> {
        return input?.photos?.mapNotNull { it?.imageUrl }.orEmpty()
    }
}