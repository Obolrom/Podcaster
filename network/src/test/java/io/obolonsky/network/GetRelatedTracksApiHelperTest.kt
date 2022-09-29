package io.obolonsky.network

import com.haroldadmin.cnradapter.NetworkResponse
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.Track
import io.obolonsky.network.api.PlainShazamApi
import io.obolonsky.network.apihelpers.GetRelatedTracksApiHelper
import io.obolonsky.network.di.modules.RemoteApiModule
import io.obolonsky.network.responses.RelatedTracksResponse
import junit.framework.TestCase
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.nio.charset.StandardCharsets

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalCoroutinesApi::class)
class GetRelatedTracksApiHelperTest : CoroutineApiHelperTest() {

    private val mockedServer by lazy { MockWebServer() }

    private val mockedPlainApi = Retrofit.Builder()
        .baseUrl(mockedServer.url("/"))
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(NetworkResponseAdapterFactory())
        .build()
        .let { retrofit ->
            RemoteApiModule().providePlainApi(retrofit)
        }

    @After
    fun shutDownServer() {
        mockedServer.shutdown()
    }

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
    fun test_504_response_code() = runTest {
        mockedServer.enqueueResponse("/get-related-tracks/server_error_504.json", 504)

        val testCoroutineSchedulers = provideTestCoroutineDispatcher(testScheduler)

        val response = GetRelatedTracksApiHelper(
            mockedPlainApi,
            testCoroutineSchedulers
        ).load("/")

        assertTrue(response is Reaction.Fail)

        assertTrue((response as Reaction.Fail).error is Error.ServerError)
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

        assertTrue((response as Reaction.Fail).error is Error.ServerError)
    }

    @Test
    fun testWithMockServer() = runTest {
        mockedServer.enqueueResponse("/get-related-tracks/real_response-200.json", 200)

        val testCoroutineSchedulers = provideTestCoroutineDispatcher(testScheduler)

        val response = GetRelatedTracksApiHelper(
            mockedPlainApi,
            testCoroutineSchedulers
        ).load("/")

        assertTrue(response is Reaction.Success)
    }

    private fun MockWebServer.enqueueResponse(fileName: String, code: Int) {
        val inputStream = javaClass.classLoader?.getResourceAsStream("api-responses/$fileName")

        val source = inputStream?.let { inputStream.source().buffer() }
        source?.let {
            enqueue(
                MockResponse()
                    .setResponseCode(code)
                    .setBody(source.readString(StandardCharsets.UTF_8))
            )
        }
    }
}