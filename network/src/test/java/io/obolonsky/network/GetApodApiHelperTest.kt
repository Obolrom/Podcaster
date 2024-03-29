package io.obolonsky.network

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.network.api.NasaApodApi
import io.obolonsky.network.apihelpers.GetApodApiHelper
import io.obolonsky.network.responses.nasa.ApodResponse
import io.reactivex.rxjava3.core.Single
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import retrofit2.HttpException
import retrofit2.Response

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalCoroutinesApi::class)
class GetApodApiHelperTest : RxApiHelperTest() {

    @Test
    fun testToTest() = runTest {
        val mockedNasaApodApi = mock<NasaApodApi> {
            on { getRandomApod(1) } doReturn Single.just(
                listOf(
                    ApodResponse(
                        date = "22.09.2022",
                        explanation = "explanation",
                        hdUrl = "someUrl",
                        mediaType = "mediaType",
                        url = "url",
                    )
                )
            )
        }

        val response = GetApodApiHelper(
            apodApi = mockedNasaApodApi,
            rxSchedulers = testRxSchedulers
        ).load(1)

        assertEquals(
            Reaction.success(listOf("url")).data,
            (response as Reaction.Success).data
        )
    }

    @Test
    fun testToTest2_unknown_error() = runTest {
        val mockedNasaApodApi = mock<NasaApodApi> {
            on { getRandomApod(1) } doThrow IllegalStateException()
        }

        val response = GetApodApiHelper(
            apodApi = mockedNasaApodApi,
            rxSchedulers = testRxSchedulers
        ).load(1)

        assertTrue(response is Reaction.Fail)
        assertTrue((response as Reaction.Fail).error is Error.UnknownError)
    }

    @Test
    fun testToTest2() = runTest {
        val mockedNasaApodApi = mock<NasaApodApi> {
            on { getRandomApod(1) } doThrow HttpException(Response.success(null))
        }

        val response = GetApodApiHelper(
            apodApi = mockedNasaApodApi,
            rxSchedulers = testRxSchedulers
        ).load(1)

        assertTrue(response is Reaction.Fail)
        assertTrue((response as Reaction.Fail).error is Error.NetworkError)
    }

    @Test
    fun testToTest3() = runTest {
        val mockedNasaApodApi = mock<NasaApodApi> {
            on { getRandomApod(2) } doReturn Single.just(
                listOf(
                    ApodResponse(
                        date = "22.09.2022",
                        explanation = "explanation",
                        hdUrl = "someUrl",
                        mediaType = "mediaType",
                        url = "url1",
                    ),
                    ApodResponse(
                        date = "21.09.2022",
                        explanation = "explanation",
                        hdUrl = "someUrl",
                        mediaType = "mediaType",
                        url = "url2",
                    )
                )
            )
        }

        val response = GetApodApiHelper(
            apodApi = mockedNasaApodApi,
            rxSchedulers = testRxSchedulers
        ).load(2)

        assertEquals(
            Reaction.success(listOf("url1", "url2")).data,
            (response as Reaction.Success).data
        )
    }
}