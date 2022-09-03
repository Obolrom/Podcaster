package io.obolonsky.network.apihelpers

import com.apollographql.apollo3.ApolloClient
import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.spaceX.rocket.Rocket
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.RocketFullDetailsQuery
import io.obolonsky.network.mappers.RocketFullDetailsToRocketMapper
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetRocketDetailsApiHelper @Inject constructor(
    private val apolloClient: ApolloClient,
    private val dispatchers: CoroutineSchedulers,
) : ApiHelperWithOneParam<Rocket?, Error, String> {

    override suspend fun load(param: String): Reaction<Rocket?, Error> {
        val response = withContext(dispatchers.io) {
            apolloClient.query(RocketFullDetailsQuery(param)).execute().data
        }

        if (response?.rocket == null)
            return Reaction.Success(null)

        val mappedResponse = withContext(dispatchers.computation) {
            RocketFullDetailsToRocketMapper.map(response.rocket)
        }

        return Reaction.Success(mappedResponse)
    }
}