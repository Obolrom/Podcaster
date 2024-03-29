package io.obolonsky.network.apihelpers.github

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import io.obolonsky.core.di.data.github.GithubRepoView
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.apihelpers.base.BaseSingleFlowGraphQlApiHelper
import io.obolonsky.network.github.GithubRepoQuery
import io.obolonsky.network.mappers.github.GithubRepoViewMapper
import io.obolonsky.network.utils.GitHub
import javax.inject.Inject

class GetGithubRepoApiHelper @Inject constructor(
    @GitHub private val githubClient: ApolloClient,
    dispatchers: CoroutineSchedulers,
) : BaseSingleFlowGraphQlApiHelper<GithubRepoQuery.Data, GithubRepoView, GetGithubRepoApiHelper.Params>(
    dispatchers = dispatchers,
    mapper = GithubRepoViewMapper()
) {

    override fun apiRequest(param: Params): ApolloCall<GithubRepoQuery.Data> {
        return githubClient.query(GithubRepoQuery(param.repo, param.owner))
    }

    data class Params(
        val owner: String,
        val repo: String,
    )
}