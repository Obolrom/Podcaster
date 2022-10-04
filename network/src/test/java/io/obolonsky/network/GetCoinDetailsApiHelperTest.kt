package io.obolonsky.network

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.network.apihelpers.GetCoinDetailsApiHelper
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.StandardCharsets

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalCoroutinesApi::class)
class GetCoinDetailsApiHelperTest : RxApiHelperTest() {

    private val mockedServer by lazy { MockWebServer() }

    private val mockedCoinPaprikaApi = Retrofit.Builder()
        .baseUrl(mockedServer.url("/"))
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
        .let { retrofit ->
            RemoteApiModule().provideCoinPaprikaApi(retrofit)
        }

    @After
    fun shutDownServer() {
        mockedServer.shutdown()
    }

    @Test
    fun `response 200 code, check for data`() = runTest {
        mockedServer.enqueueResponse("real_response_200.json")

        val response = GetCoinDetailsApiHelper(
            coinPaprikaApi = mockedCoinPaprikaApi,
            rxSchedulers = testRxSchedulers
        ).load(GetCoinDetailsApiHelper.QueryParams("id"))

        assertTrue(response is Reaction.Success)

        (response as Reaction.Success).data.let { coin ->
            assertEquals("Bitcoin", coin.name)
            assertEquals(1, coin.rank)
            assertEquals("coin", coin.type)
        }
    }

    @Test
    fun `bad response with nullable fields`() = runTest {
        mockedServer.enqueueResponse("bad_response_null_fields.json")

        val response = GetCoinDetailsApiHelper(
            coinPaprikaApi = mockedCoinPaprikaApi,
            rxSchedulers = testRxSchedulers
        ).load(GetCoinDetailsApiHelper.QueryParams("id"))

        assertEquals(
            true,
            response is Reaction.Fail,
        )

        assertEquals(
            Error.UnknownError::class.java,
            (response as Reaction.Fail).error::class.java
        )
    }

    private fun MockWebServer.enqueueResponse(fileName: String, statusCode: Int = 200) {
        val inputStream = javaClass.classLoader
            ?.getResourceAsStream("api-responses/coin-details/$fileName")

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