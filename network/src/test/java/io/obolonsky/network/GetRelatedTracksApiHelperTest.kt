package io.obolonsky.network

import com.haroldadmin.cnradapter.NetworkResponse
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.network.api.PlainShazamApi
import io.obolonsky.network.apihelpers.GetRelatedTracksApiHelper
import io.obolonsky.network.di.modules.RemoteApiModule
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
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
    fun `response 200 code, check mapper for empty response`() = runTest {
        mockedServer.enqueueResponse("/get-related-tracks/empty_response_200.json")

        val testCoroutineSchedulers = provideTestCoroutineDispatcher(testScheduler)

        val response = GetRelatedTracksApiHelper(
            mockedPlainApi,
            testCoroutineSchedulers
        ).load("url")

        assertTrue(response is Reaction.Success)

        (response as? Reaction.Success)?.data?.let { relatedTracks ->
            assertTrue(relatedTracks.isEmpty())
        }
    }

    @Test
    fun `response 504 code, check for server error`() = runTest {
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
    fun `response 200 code, check result list size`() = runTest {
        mockedServer.enqueueResponse("/get-related-tracks/for_nullable_check.json")

        val testCoroutineSchedulers = provideTestCoroutineDispatcher(testScheduler)

        val response = GetRelatedTracksApiHelper(
            mockedPlainApi,
            testCoroutineSchedulers
        ).load("/")

        assertTrue(response is Reaction.Success)

        (response as Reaction.Success).data.forEach {
            it.audioUri?.length
        }
    }

    @Test
    fun `check for probable network error`() = runTest {
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
    fun `response 400 code, check for server error`() = runTest {
        mockedServer.enqueueResponse("/get-related-tracks/real_response-200.json", 400)

        val testCoroutineSchedulers = provideTestCoroutineDispatcher(testScheduler)

        val response = GetRelatedTracksApiHelper(
            mockedPlainApi,
            testCoroutineSchedulers
        ).load("sampleUrl")

        assertTrue(response is Reaction.Fail)

        assertTrue((response as Reaction.Fail).error is Error.ServerError)
    }

    @Test
    fun `response 200 code with checking result fields`() = runTest {
        mockedServer.enqueueResponse("/get-related-tracks/real_response-200.json")

        val testCoroutineSchedulers = provideTestCoroutineDispatcher(testScheduler)

        val response = GetRelatedTracksApiHelper(
            mockedPlainApi,
            testCoroutineSchedulers
        ).load("/")

        assertTrue(response is Reaction.Success)

        assertEquals((response as Reaction.Success).data.size, 20)

        response.data.first().let { firstTrack ->
            assertEquals(null, firstTrack.audioUri)
            assertEquals("Meaux Green", firstTrack.subtitle)
            assertEquals(0, firstTrack.imageUrls.size)
        }
    }

    private fun MockWebServer.enqueueResponse(fileName: String, statusCode: Int = 200) {
        val inputStream = javaClass.classLoader?.getResourceAsStream("api-responses/$fileName")

        val source = inputStream?.let { inputStream.source().buffer() }
        source?.let {
            enqueue(
                MockResponse()
                    .setResponseCode(statusCode)
                    .setBody(source.readString(StandardCharsets.UTF_8))
            )
        }
    }
}