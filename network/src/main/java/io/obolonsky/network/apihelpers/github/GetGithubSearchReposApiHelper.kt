package io.obolonsky.network.apihelpers.github

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import io.obolonsky.core.di.data.github.GithubRepository
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.apihelpers.base.GraphQlApiHelper
import io.obolonsky.network.github.GithubRepositoriesSearchQuery
import io.obolonsky.network.mappers.github.GithubSearchReposMapper
import io.obolonsky.network.utils.GitHub
import javax.inject.Inject

class GetGithubSearchReposApiHelper @Inject constructor(
    @GitHub private val githubClient: ApolloClient,
    dispatchers: CoroutineSchedulers,
) : GraphQlApiHelper<GithubRepositoriesSearchQuery.Data, List<GithubRepository>, String>(
    dispatchers = dispatchers,
    mapper = GithubSearchReposMapper()
) {

    override fun apiRequest(param: String): ApolloCall<GithubRepositoriesSearchQuery.Data> {
        return githubClient.query(GithubRepositoriesSearchQuery(10, "$param in:name"))
    }
}