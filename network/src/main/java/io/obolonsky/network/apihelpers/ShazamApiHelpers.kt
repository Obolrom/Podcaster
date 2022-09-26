package io.obolonsky.network.apihelpers

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.api.PlainShazamApi
import io.obolonsky.network.api.SongRecognitionApi
import io.obolonsky.network.mappers.SongRecognizeResponseToShazamDetectMapper
import io.obolonsky.network.mappers.TrackResponseToTrackMapper
import io.obolonsky.network.utils.runWithReaction
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

const val FILE = "file"

class ShazamSongRecognitionApiHelper @Inject constructor(
    private val songRecognitionApi: SongRecognitionApi,
    private val dispatchers: CoroutineSchedulers,
) : ApiHelper<ShazamDetect, File> {

    override suspend fun load(param: File): Reaction<ShazamDetect, Error> {
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                name = FILE,
                filename = param.name,
                body = param.asRequestBody()
            )
            .build()

        val shazamDetect = withContext(dispatchers.io) {
            songRecognitionApi.detect(body)
        }

        return shazamDetect.runWithReaction {
            SongRecognizeResponseToShazamDetectMapper.map(this)
        }
    }
}

class GetRelatedTracksApiHelper @Inject constructor(
    private val plainShazamApi: PlainShazamApi,
) : ApiHelper<List<Track>, String> {

    override suspend fun load(
        param: String
    ): Reaction<List<Track>, Error> = plainShazamApi.getRelatedTracks(param)
        .runWithReaction { tracks.map(TrackResponseToTrackMapper::map) }
}