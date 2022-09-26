package io.obolonsky.network.apihelpers

import com.apollographql.apollo3.ApolloClient
import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.spaceX.rocket.Rocket
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.RocketFullDetailsQuery
import io.obolonsky.network.apihelpers.base.ApiHelper
import io.obolonsky.network.mappers.RocketFullDetailsToRocketMapper
import io.obolonsky.network.utils.runWithReaction
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetRocketDetailsApiHelper @Inject constructor(
    private val apolloClient: ApolloClient,
    private val dispatchers: CoroutineSchedulers,
) : ApiHelper<Rocket?, String> {

    override suspend fun load(param: String): Reaction<Rocket?, Error> = runWithReaction {
        val result = withContext(dispatchers.io) {
            apolloClient.query(RocketFullDetailsQuery(param))
                .execute()
                .dataAssertNoErrors
        }

        if (result.rocket == null) null
        else withContext(dispatchers.computation) {
            RocketFullDetailsToRocketMapper.map(result.rocket)
        }
    }
}