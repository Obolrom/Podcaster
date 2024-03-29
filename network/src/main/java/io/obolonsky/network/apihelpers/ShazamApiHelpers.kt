package io.obolonsky.network.apihelpers

import com.haroldadmin.cnradapter.NetworkResponse
import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.api.SongRecognitionApi
import io.obolonsky.network.apihelpers.base.CoroutinesApiHelper
import io.obolonsky.network.mappers.SongRecognizeResponseToShazamDetectMapper
import io.obolonsky.network.mappers.TrackResponseToTrackListMapper
import io.obolonsky.network.responses.RelatedTracksResponse
import io.obolonsky.network.responses.SongRecognizeResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

const val FILE = "file"

class ShazamSongRecognitionApiHelper @Inject constructor(
    private val songRecognitionApi: SongRecognitionApi,
    dispatchers: CoroutineSchedulers,
) : CoroutinesApiHelper<SongRecognizeResponse, ShazamDetect, File>(
    dispatchers = dispatchers,
    mapper = SongRecognizeResponseToShazamDetectMapper,
) {

    override suspend fun apiRequest(param: File): NetworkResponse<SongRecognizeResponse, *> {
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("upload_file", param.name,
                param.asRequestBody("text/plain".toMediaTypeOrNull())
            )
            .build()

        return songRecognitionApi.detect(body)
    }
}

class GetRelatedTracksApiHelper @Inject constructor(
    private val songRecognitionApi: SongRecognitionApi,
    dispatchers: CoroutineSchedulers,
) : CoroutinesApiHelper<RelatedTracksResponse.Result, List<Track>, String>(
    dispatchers = dispatchers,
    mapper = TrackResponseToTrackListMapper,
) {

    override suspend fun apiRequest(param: String): NetworkResponse<RelatedTracksResponse.Result, *> {
        return songRecognitionApi.getRelatedTracks(param)
    }
}