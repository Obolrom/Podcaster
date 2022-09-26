package io.obolonsky.network.apihelpers

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.LaunchNextQuery
import io.obolonsky.network.apihelpers.base.GraphQlApiHelper
import javax.inject.Inject

class GetLaunchNextApiHelper @Inject constructor(
    private val apolloClient: ApolloClient,
    dispatchers: CoroutineSchedulers,
) : GraphQlApiHelper<LaunchNextQuery.Data, Boolean, Unit>(
    dispatchers = dispatchers,
    mapper = object : Mapper<LaunchNextQuery.Data, Boolean> {
        override fun map(input: LaunchNextQuery.Data) = true
    }
) {

    override fun apiRequest(param: Unit): ApolloCall<LaunchNextQuery.Data> {
        return apolloClient.query(LaunchNextQuery())
    }
}