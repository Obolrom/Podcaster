package io.obolonsky.network.apihelpers

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import io.obolonsky.core.di.data.spaceX.rocket.Rocket
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.apihelpers.base.GraphQlApiHelper
import io.obolonsky.network.mappers.RocketFullDetailsToRocketMapper
import io.obolonsky.network.spacex.RocketFullDetailsQuery
import javax.inject.Inject

class GetRocketDetailsApiHelper @Inject constructor(
    private val apolloClient: ApolloClient,
    dispatchers: CoroutineSchedulers,
) : GraphQlApiHelper<RocketFullDetailsQuery.Data, Rocket?, String>(
    dispatchers = dispatchers,
    mapper = RocketFullDetailsToRocketMapper
) {

    override fun apiRequest(param: String): ApolloCall<RocketFullDetailsQuery.Data> {
        return apolloClient.query(RocketFullDetailsQuery(param))
    }
}