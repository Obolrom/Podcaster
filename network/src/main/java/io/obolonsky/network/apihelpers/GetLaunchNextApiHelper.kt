package io.obolonsky.network.apihelpers

import com.apollographql.apollo3.ApolloClient
import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.LaunchNextQuery
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class GetLaunchNextApiHelper @Inject constructor(
    private val apolloClient: ApolloClient,
    private val dispatchers: CoroutineSchedulers,
) : ApiHelper<LaunchNextQuery.Data?, Error> {

    override suspend fun load(): Reaction<LaunchNextQuery.Data?, Error> {
        val fromGraphQl = withContext(dispatchers.io) {
            apolloClient.query(LaunchNextQuery()).execute()
        }
        val response = fromGraphQl.data
        Timber.d("dataFromGraphQl response: $response")

        return Reaction.Success(response)
    }
}