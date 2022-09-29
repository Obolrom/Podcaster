package io.obolonsky.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.annotations.ApolloExperimental
import com.apollographql.apollo3.mockserver.MockServer
import com.apollographql.apollo3.mockserver.enqueue
import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.apihelpers.GetLaunchNextApiHelper
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.nio.charset.StandardCharsets

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED", "BlockingMethodInNonBlockingContext")
@OptIn(ExperimentalCoroutinesApi::class, ApolloExperimental::class)
class GetLaunchNextApiHelperTest {

    private val mockServer by lazy {
        MockServer()
    }

    private lateinit var apolloClient: ApolloClient

    @Before
    fun setup() = runTest {
        apolloClient = ApolloClient.Builder()
            .serverUrl(mockServer.url())
            .build()
    }

    @After
    fun shutdownMockServer() = runTest {
        mockServer.stop()
    }

    @Test
    fun testGraphQlQuery() = runTest {
        mockServer.enqueue("""
            {"data":{"launchNext":{"id":"110","is_tentative":false,"launch_date_local":"2020-12-06T11:17:00-05:00","links":{"flickr_images":[],"article_link":null,"video_link":null},"rocket":{"rocket_name":"Falcon 9"}}}}
        """.trimIndent())

        val testCoroutineScheduler = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testCoroutineScheduler)

        val response = GetLaunchNextApiHelper(
            apolloClient = apolloClient,
            dispatchers = object : CoroutineSchedulers {
                override val main = testCoroutineScheduler
                override val io = testCoroutineScheduler
                override val computation = testCoroutineScheduler
                override val unconfined = testCoroutineScheduler
            }
        ).load(Unit)

        assertTrue(response is Reaction.Success)
        assertEquals(
            (response as Reaction.Success).data,
            true
        )
    }

    @Test
    fun test_200_good_response() = runTest {
        mockServer.enqueueRequest("launch_next_200.json")

        val testCoroutineScheduler = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testCoroutineScheduler)

        val response = GetLaunchNextApiHelper(
            apolloClient = apolloClient,
            dispatchers = object : CoroutineSchedulers {
                override val main = testCoroutineScheduler
                override val io = testCoroutineScheduler
                override val computation = testCoroutineScheduler
                override val unconfined = testCoroutineScheduler
            }
        ).load(Unit)

        assertTrue(response is Reaction.Success)
    }

    @Test
    fun testGraphQlQueryFail() = runTest {
        mockServer.enqueue(
            string = "",
            statusCode = 401,
        )

        val testCoroutineScheduler = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testCoroutineScheduler)

        val response = GetLaunchNextApiHelper(
            apolloClient = apolloClient,
            dispatchers = object : CoroutineSchedulers {
                override val main = testCoroutineScheduler
                override val io = testCoroutineScheduler
                override val computation = testCoroutineScheduler
                override val unconfined = testCoroutineScheduler
            }
        ).load(Unit)

        assertTrue(response is Reaction.Fail)
        assertTrue((response as Reaction.Fail).error is Error.UnknownError)
    }

    private fun MockServer.enqueueRequest(fileName: String, statusCode: Int = 200) {
        val inputStream = javaClass.classLoader
            ?.getResourceAsStream("api-responses/launch-next-query/$fileName")

        val source = inputStream?.let { inputStream.source().buffer() }
        source?.let {
            enqueue(
                string = source.readString(StandardCharsets.UTF_8),
                statusCode = statusCode,
            )
        }
    }
}