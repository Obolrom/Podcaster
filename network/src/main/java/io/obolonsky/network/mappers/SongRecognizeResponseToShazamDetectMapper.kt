package io.obolonsky.network.mappers

import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.core.di.utils.NO_ID
import io.obolonsky.network.responses.SongRecognizeResponse

object SongRecognizeResponseToShazamDetectMapper :
    Mapper<SongRecognizeResponse, ShazamDetect> {

    override fun map(input: SongRecognizeResponse): ShazamDetect {
        return ShazamDetect(
            tagId = input.tagId ?: NO_ID,
            track = input.track?.let(TrackResponseToTrackMapper::map)
        )
    }
}