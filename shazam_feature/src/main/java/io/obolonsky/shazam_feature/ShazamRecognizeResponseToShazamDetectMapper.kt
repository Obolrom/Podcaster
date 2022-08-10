package io.obolonsky.shazam_feature

import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.utils.Mapper

const val NO_ID = "NO_ID"
const val AUDIO_TRACK_HUB_LINK_INDEX = 1

object SongRecognizeResponseToShazamDetectMapper : Mapper<SongRecognizeResponse, ShazamDetect> {

    override fun map(input: SongRecognizeResponse): ShazamDetect {
        return ShazamDetect(
            tagId = input.tagId ?: NO_ID,
            track = input.track?.let(TrackResponseToTrackMapper::map)
        )
    }
}

object TrackResponseToTrackMapper : Mapper<SongRecognizeResponse.TrackResponse, Track> {

    override fun map(input: SongRecognizeResponse.TrackResponse): Track {
        return Track(
            audioUri = input.hub
                ?.actions
                ?.getOrNull(AUDIO_TRACK_HUB_LINK_INDEX)
                ?.uri
        )
    }
}