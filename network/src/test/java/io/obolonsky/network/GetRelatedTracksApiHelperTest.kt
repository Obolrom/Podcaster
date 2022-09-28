package io.obolonsky.network

import com.haroldadmin.cnradapter.NetworkResponse
import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.Track
import io.obolonsky.network.api.PlainShazamApi
import io.obolonsky.network.apihelpers.GetRelatedTracksApiHelper
import io.obolonsky.network.responses.RelatedTracksResponse
import junit.framework.TestCase
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import retrofit2.Response
import java.io.IOException

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalCoroutinesApi::class)
class GetRelatedTracksApiHelperTest : CoroutineApiHelperTest() {

    @Test
    fun testGetRelatedTracksApiHelper() = runTest {
        val mockedShazamApi = mock<PlainShazamApi> {
            onBlocking { getRelatedTracks("url") } doReturn NetworkResponse.Success(
                body = RelatedTracksResponse(
                    emptyList()
                ),
                response = Response.success(
                    RelatedTracksResponse(
                    emptyList()
                )
                )
            )
        }

        val testCoroutineSchedulers = provideTestCoroutineDispatcher(testScheduler)

        val response = GetRelatedTracksApiHelper(
            mockedShazamApi,
            testCoroutineSchedulers
        ).load("url")

        TestCase.assertEquals(
            Reaction.Success<List<Track>>(emptyList()).data,
            (response as Reaction.Success).data
        )
    }

    @Test
    fun testGetRelatedTracksApiHelper2() = runTest {
        val mockedShazamApi = mock<PlainShazamApi> {
            onBlocking {
                getRelatedTracks("sampleUrl")
            } doReturn NetworkResponse.NetworkError(IOException("IoException"))
        }

        val testCoroutineSchedulers = provideTestCoroutineDispatcher(testScheduler)

        val response = GetRelatedTracksApiHelper(
            mockedShazamApi,
            testCoroutineSchedulers
        ).load("sampleUrl")

        assertTrue(response is Reaction.Fail)

        assertTrue((response as Reaction.Fail).error is Error.NetworkError)
    }

    @Test
    fun testGetRelatedTracksApiHelper3() = runTest {
        val mockedShazamApi = mock<PlainShazamApi> {
            onBlocking {
                getRelatedTracks("sampleUrl")
            } doReturn NetworkResponse.ServerError(
                null,
                Response.error<RelatedTracksResponse>(
                    400,
                    "raw response body as string".toResponseBody("application/json".toMediaTypeOrNull())
                )
            )
        }

        val testCoroutineSchedulers = provideTestCoroutineDispatcher(testScheduler)

        val response = GetRelatedTracksApiHelper(
            mockedShazamApi,
            testCoroutineSchedulers
        ).load("sampleUrl")

        assertTrue(response is Reaction.Fail)

        assertTrue((response as Reaction.Fail).error is Error.UnknownError)
    }
}