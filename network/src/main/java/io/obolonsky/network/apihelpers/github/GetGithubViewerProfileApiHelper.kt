package io.obolonsky.network.apihelpers.github

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import io.obolonsky.core.di.data.github.GithubUserProfile
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.apihelpers.base.BaseSingleFlowGraphQlApiHelper
import io.obolonsky.network.github.GetGithubViewerProfileQuery
import io.obolonsky.network.mappers.github.GithubViewerProfileMapper
import io.obolonsky.network.utils.GitHub
import javax.inject.Inject

class GetGithubViewerProfileApiHelper @Inject constructor(
    @GitHub private val githubClient: ApolloClient,
    dispatchers: CoroutineSchedulers,
) : BaseSingleFlowGraphQlApiHelper<GetGithubViewerProfileQuery.Data, GithubUserProfile, Unit>(
    dispatchers = dispatchers,
    mapper = GithubViewerProfileMapper()
) {

    override fun apiRequest(param: Unit): ApolloCall<GetGithubViewerProfileQuery.Data> {
        return githubClient.query(GetGithubViewerProfileQuery())
    }
}