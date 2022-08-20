package io.obolonsky.shazam_feature.data.mappers

import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.shazam_feature.data.responses.SongRecognizeResponse

internal object SongRecognizeResponseToShazamDetectMapper :
    Mapper<SongRecognizeResponse, ShazamDetect> {

    override fun map(input: SongRecognizeResponse): ShazamDetect {
        return ShazamDetect(
            tagId = input.tagId ?: NO_ID,
            track = input.track?.let(TrackResponseToTrackMapper::map)
        )
    }
}