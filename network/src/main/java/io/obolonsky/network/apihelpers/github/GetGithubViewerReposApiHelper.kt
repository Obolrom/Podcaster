package io.obolonsky.network.apihelpers.github

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import io.obolonsky.core.di.data.github.GithubRepoView
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.apihelpers.base.BaseSingleFlowGraphQlApiHelper
import io.obolonsky.network.github.GithubViewerReposQuery
import io.obolonsky.network.mappers.github.ViewerReposMapper
import io.obolonsky.network.utils.GitHub
import javax.inject.Inject

class GetGithubViewerReposApiHelper @Inject constructor(
    @GitHub private val githubClient: ApolloClient,
    dispatchers: CoroutineSchedulers,
) : BaseSingleFlowGraphQlApiHelper<GithubViewerReposQuery.Data, List<GithubRepoView>, Unit>(
    dispatchers = dispatchers,
    mapper = ViewerReposMapper()
) {

    override fun apiRequest(param: Unit): ApolloCall<GithubViewerReposQuery.Data> {
        return githubClient.query(GithubViewerReposQuery())
    }
}