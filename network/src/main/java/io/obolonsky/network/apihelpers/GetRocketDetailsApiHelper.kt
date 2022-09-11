package io.obolonsky.network.apihelpers

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.spaceX.rocket.Rocket
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.RocketFullDetailsQuery
import io.obolonsky.network.mappers.RocketFullDetailsToRocketMapper
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class GetRocketDetailsApiHelper @Inject constructor(
    private val apolloClient: ApolloClient,
    private val dispatchers: CoroutineSchedulers,
) : ApiHelper<Rocket?, String> {

    override suspend fun load(param: String): Reaction<Rocket?, Error> {
        val response = try {
            withContext(dispatchers.io) {
                apolloClient.query(RocketFullDetailsQuery(param))
                    .execute()
                    .dataAssertNoErrors
            }
        } catch (exc: ApolloException) {
            Timber.e(exc)
            return Reaction.Fail(Error.NetworkError(exc))
        }

        if (response.rocket == null)
            return Reaction.Success(null)

        val mappedResponse = withContext(dispatchers.computation) {
            RocketFullDetailsToRocketMapper.map(response.rocket)
        }

        return Reaction.Success(mappedResponse)
    }
}