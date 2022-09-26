package io.obolonsky.network.mappers

import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.responses.nasa.ApodResponse

object ApodResponseToImageUrlsMapper : Mapper<List<ApodResponse>, List<String>> {

    override fun map(input: List<ApodResponse>): List<String> {
        return input.mapNotNull { it.url }
    }
}