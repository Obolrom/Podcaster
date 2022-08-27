package io.obolonsky.repository

import android.content.Context
import com.google.gson.Gson
import com.haroldadmin.cnradapter.NetworkResponse
import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.repositories.ShazamRepo
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
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class ShazamRepository @Inject constructor(
    private val context: Context,
    private val songRecognitionApi: SongRecognitionApi,
    private val plainShazamApi: PlainShazamApi,
    private val dispatchers: CoroutineSchedulers,
) : ShazamRepo {

    override suspend fun audioDetect(audioFile: File): ShazamDetect? {
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                name = FILE,
                filename = audioFile.name,
                body = audioFile.asRequestBody()
            )
            .build()

        val shazamDetect = withContext(dispatchers.io) {
            // api
//            songRecognitionApi.detect(body)

            val data = String(context.assets.open("response.json").readBytes())
            Timber.d("fuckIt data: $data")
            val detectSampleResponse = Gson().fromJson(data, SongRecognizeResponse::class.java)
            NetworkResponse.Success<SongRecognizeResponse, Unit>(detectSampleResponse, Response.success(detectSampleResponse)) as NetworkResponse<SongRecognizeResponse, Unit>
        }
        when (shazamDetect) {
            is NetworkResponse.Success -> {
                val detected = SongRecognizeResponseToShazamDetectMapper.map(shazamDetect.body)
                Timber.d("shazamApi success ${shazamDetect.body}")

                return detected
            }

            is NetworkResponse.Error -> {
                Timber.d("shazamApi error ${shazamDetect.error}")
            }
        }

        return null
    }

    override suspend fun getRelatedTracks(url: String): List<Track> {

        when (val response = plainShazamApi.getRelatedTracks(url)) {
            is NetworkResponse.Success -> {
                return response.body.tracks.map(TrackResponseToTrackMapper::map)
            }

            is NetworkResponse.Error -> { }
        }

        return emptyList()
    }

    private companion object {
        const val FILE = "file"
    }
}