package io.obolonsky.podcaster.data.repositories

import android.content.Context
import com.google.gson.Gson
import com.haroldadmin.cnradapter.NetworkResponse
import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.scopes.ApplicationScope
import io.obolonsky.podcaster.di.modules.CoroutineSchedulers
import io.obolonsky.shazam_feature.ShazamDetectResponse
import io.obolonsky.shazam_feature.SongRecognitionApi
import io.obolonsky.shazam_feature.SongRecognizeResponse
import io.obolonsky.shazam_feature.SongRecognizeResponseToShazamDetectMapper
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@ApplicationScope
class ShazamRepository @Inject constructor(
    private val context: Context,
    private val songRecognitionApi: SongRecognitionApi,
    private val dispatchers: CoroutineSchedulers,
) {

    suspend fun audioDetect(audioFile: File): ShazamDetect? {
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

    private companion object {
        const val FILE = "file"
    }
}