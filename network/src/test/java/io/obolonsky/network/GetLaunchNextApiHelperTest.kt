package io.obolonsky.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.annotations.ApolloExperimental
import com.apollographql.apollo3.mockserver.MockServer
import com.apollographql.apollo3.mockserver.enqueue
import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
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
class GetLaunchNextApiHelperTest : TestCoroutineDispatcherProvider {

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
    fun `response 200 code, check for return data`() = runTest {
        mockServer.enqueueRequest("launch_next_200.json")

        val testCoroutineDispatchers = provideTestCoroutineDispatcher(testScheduler)
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        val response = GetLaunchNextApiHelper(
            apolloClient = apolloClient,
            dispatchers = testCoroutineDispatchers,
        ).load(Unit)

        assertTrue(response is Reaction.Success)

        assertEquals(
            true,
            (response as Reaction.Success).data
        )
    }

    @Test
    fun `partial response, check for error`() = runTest {
        mockServer.enqueueRequest("partial_response.json")

        val testCoroutineDispatchers = provideTestCoroutineDispatcher(testScheduler)
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        val response = GetLaunchNextApiHelper(
            apolloClient = apolloClient,
            dispatchers = testCoroutineDispatchers,
        ).load(Unit)

        assertTrue(response is Reaction.Fail)

        assertEquals(
            Error.UnknownError::class.java,
            (response as Reaction.Fail).error::class.java
        )
    }

    @Test
    fun `response 401 code, check for error`() = runTest {
        mockServer.enqueueRequest(
            "launch_next_200.json",
            statusCode = 401,
        )

        val testCoroutineDispatchers = provideTestCoroutineDispatcher(testScheduler)
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        val response = GetLaunchNextApiHelper(
            apolloClient = apolloClient,
            dispatchers = testCoroutineDispatchers,
        ).load(Unit)

        assertTrue(response is Reaction.Fail)

        assertEquals(
            Error.ServerError::class.java,
            (response as Reaction.Fail).error::class.java
        )
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