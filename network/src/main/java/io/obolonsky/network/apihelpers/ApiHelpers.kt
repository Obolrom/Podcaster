package io.obolonsky.network.apihelpers

import android.content.Context
import com.google.gson.Gson
import com.haroldadmin.cnradapter.NetworkResponse
import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.FeatureToggles
import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.api.PlainShazamApi
import io.obolonsky.network.api.SongRecognitionApi
import io.obolonsky.network.mappers.SongRecognizeResponseToShazamDetectMapper
import io.obolonsky.network.mappers.TrackResponseToTrackMapper
import io.obolonsky.network.responses.SongRecognizeResponse
import io.obolonsky.network.utils.ProductionTypes
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
    private val featureToggleApiHelper: FeatureToggleApiHelper,
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

        val shazamFeatureToggle = getShazamFeatureToggle().shazam

        val shazamDetect = withContext(dispatchers.io) {
            if (shazamFeatureToggle.isWorking) {
                songRecognitionApi.detect(body)
            } else {
                val data = String(context.assets.open("response.json").readBytes())
                val detectSampleResponse = Gson().fromJson(data, SongRecognizeResponse::class.java)
                NetworkResponse.Success<SongRecognizeResponse, Unit>(
                    detectSampleResponse,
                    Response.success(detectSampleResponse)
                )
            }
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

    private suspend fun getShazamFeatureToggle(): FeatureToggles {
        return when (val shazamFeatureToggle = featureToggleApiHelper.load(ProductionTypes.PROD)) {
            is Reaction.Success -> {
                shazamFeatureToggle.data ?: FeatureToggles(
                    shazam = FeatureToggles.Shazam(
                        isWorking = false,
                    )
                )
            }
            is Reaction.Fail -> {
                FeatureToggles(
                    shazam = FeatureToggles.Shazam(
                        isWorking = false,
                    )
                )
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