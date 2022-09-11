package io.obolonsky.network.apihelpers

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
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
) : ApiHelper<LaunchNextQuery.Data?, Unit> {

    override suspend fun load(param: Unit): Reaction<LaunchNextQuery.Data?, Error> {
        val data = try {
            withContext(dispatchers.io) {
                apolloClient.query(LaunchNextQuery())
                    .execute()
                    .dataAssertNoErrors
            }
        } catch (exc: ApolloException) {
            Timber.e(exc)
            return Reaction.Fail(Error.NetworkError(exc))
        }

        return Reaction.Success(data)
    }
}