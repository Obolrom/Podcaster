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
import org.junit.Test

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalCoroutinesApi::class, ApolloExperimental::class)
class GetLaunchNextApiHelperTest {

    @Test
    fun testGraphQlQuery() = runTest {
        val mockServer = MockServer()
        val apolloClient = ApolloClient.Builder()
            .serverUrl(mockServer.url())
            .build()

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

        mockServer.stop()
    }

    @Test
    fun testGraphQlQueryFail() = runTest {
        val mockServer = MockServer()
        val apolloClient = ApolloClient.Builder()
            .serverUrl(mockServer.url())
            .build()

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

        mockServer.stop()
    }
}