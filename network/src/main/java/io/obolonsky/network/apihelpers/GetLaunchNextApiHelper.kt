package io.obolonsky.network.apihelpers

import com.apollographql.apollo3.ApolloClient
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.apihelpers.base.ApiHelper
import io.obolonsky.network.spacex.LaunchNextQuery
import io.obolonsky.network.utils.runWithReaction
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetLaunchNextApiHelper @Inject constructor(
    private val apolloClient: ApolloClient,
    private val dispatchers: CoroutineSchedulers,
) : ApiHelper<Boolean, Unit> {

    override suspend fun load(param: Unit): Reaction<Boolean> {
        val data = runWithReaction {
            withContext(dispatchers.io) {
                apolloClient.query(LaunchNextQuery())
                    .execute()
                    .dataAssertNoErrors
            }
        }

        return when (data) {
            is Reaction.Success -> {
                Reaction.success(true)
            }
            is Reaction.Fail -> Reaction.fail(data.error)
        }
    }
}