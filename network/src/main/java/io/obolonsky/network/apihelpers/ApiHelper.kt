package io.obolonsky.network.apihelpers

import android.content.Context
import com.google.gson.Gson
import com.haroldadmin.cnradapter.NetworkResponse
import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.api.PlainShazamApi
import io.obolonsky.network.api.SongRecognitionApi
import io.obolonsky.network.mappers.SongRecognizeResponseToShazamDetectMapper
import io.obolonsky.network.mappers.TrackResponseToTrackMapper
import io.obolonsky.network.responses.SongRecognizeResponse
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

const val FILE = "file"

interface ApiHelperWithOneParam<D, E : Error, P> {

    suspend fun load(param: P): Reaction<D, E>
}

class ShazamSongRecognitionApiHelper @Inject constructor(
    private val context: Context,
    private val songRecognitionApi: SongRecognitionApi,
    private val dispatchers: CoroutineSchedulers,
) : ApiHelperWithOneParam<ShazamDetect, Error, File> {

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
//            songRecognitionApi.detect(body)

            val data = String(context.assets.open("response.json").readBytes())
            val detectSampleResponse = Gson().fromJson(data, SongRecognizeResponse::class.java)
            NetworkResponse.Success<SongRecognizeResponse, Unit>(
                detectSampleResponse,
                Response.success(detectSampleResponse)
            ) as NetworkResponse<SongRecognizeResponse, Unit>
        }

        return when (shazamDetect) {
            is NetworkResponse.Success -> {
                val detected = SongRecognizeResponseToShazamDetectMapper.map(shazamDetect.body)
                Reaction.Success(detected)
            }

            is NetworkResponse.Error -> {
                Reaction.Fail(Error.NetworkError())
            }
        }
    }
}

class GetRelatedTracksApiHelper @Inject constructor(
    private val plainShazamApi: PlainShazamApi,
) : ApiHelperWithOneParam<List<Track>, Error, String> {

    override suspend fun load(param: String): Reaction<List<Track>, Error> {
        return when (val response = plainShazamApi.getRelatedTracks(param)) {
            is NetworkResponse.Success -> {
                Reaction.Success(response.body.tracks.map(TrackResponseToTrackMapper::map))
            }

            is NetworkResponse.Error -> {
                Reaction.Fail(Error.NetworkError(response.error))
            }
        }
    }
}