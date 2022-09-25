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
import io.obolonsky.network.BuildConfig
import io.obolonsky.network.api.PlainShazamApi
import io.obolonsky.network.api.SongRecognitionApi
import io.obolonsky.network.mappers.SongRecognizeResponseToShazamDetectMapper
import io.obolonsky.network.mappers.TrackResponseToTrackMapper
import io.obolonsky.network.responses.SongRecognizeResponse
import io.obolonsky.network.utils.runWithReaction
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

const val FILE = "file"

class ShazamSongRecognitionApiHelper @Inject constructor(
    private val context: Context,
    private val songRecognitionApi: SongRecognitionApi,
    private val featureToggleApiHelper: FeatureToggleApiHelper,
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

        return shazamDetect.runWithReaction {
            SongRecognizeResponseToShazamDetectMapper.map(this)
        }
    }

    private suspend fun getShazamFeatureToggle(): FeatureToggles = when (
        val shazamFeatureToggle = featureToggleApiHelper.load(param = BuildConfig.PRODUCTION_TYPE)
    ) {
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

class GetRelatedTracksApiHelper @Inject constructor(
    private val plainShazamApi: PlainShazamApi,
) : ApiHelper<List<Track>, String> {

    override suspend fun load(
        param: String
    ): Reaction<List<Track>, Error> = plainShazamApi.getRelatedTracks(param)
        .runWithReaction { tracks.map(TrackResponseToTrackMapper::map) }
}