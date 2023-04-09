package io.obolonsky.network.mappers

import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.utils.AUDIO_TRACK_HUB_LINK_INDEX
import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.responses.RelatedTracksResponse
import io.obolonsky.network.responses.SongRecognizeResponse
import okhttp3.internal.toImmutableList

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
            imageUrls = imageUrls.toImmutableList(),
            relatedTracksUrl = input.relatedTracksUrl,
            relatedTracks = emptyList()
        )
    }
}

object TrackResponseToTrackListMapper : Mapper<RelatedTracksResponse, List<Track>> {

    override fun map(input: RelatedTracksResponse): List<Track> {
        return input.result
            ?.tracks
            ?.map(TrackResponseToTrackMapper::map)
            .orEmpty()
    }
}