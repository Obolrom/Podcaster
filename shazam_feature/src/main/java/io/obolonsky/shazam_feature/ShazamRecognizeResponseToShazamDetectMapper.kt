package io.obolonsky.shazam_feature

import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.utils.Mapper
import okhttp3.internal.toImmutableList

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
        val audioUri = input.hub
            ?.actions
            ?.getOrNull(AUDIO_TRACK_HUB_LINK_INDEX)
            ?.uri
        val imageUrls = mutableListOf<String>()

        input.images?.apply {
            if (backgroundUrl != null) imageUrls.add(backgroundUrl)
            if (coverArtHqUrl != null) imageUrls.add(coverArtHqUrl)
            if (coverArtUrl != null) imageUrls.add(coverArtUrl)
            if (joecolorUrl != null) imageUrls.add(joecolorUrl)
        }

        return Track(
            audioUri = audioUri,
            subtitle = input.subtitle,
            title = input.title,
            imageUrls = imageUrls.toImmutableList()
        )
    }
}