package io.obolonsky.network.apihelpers.github

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.apihelpers.base.BaseSingleFlowGraphQlApiHelper
import io.obolonsky.network.github.GithubRepoBranchesQuery
import io.obolonsky.network.mappers.github.RepoBranchesMapper
import io.obolonsky.network.utils.GitHub
import javax.inject.Inject

class GetGithubRepoBranches @Inject constructor(
    @GitHub private val githubClient: ApolloClient,
    dispatchers: CoroutineSchedulers,
) : BaseSingleFlowGraphQlApiHelper<GithubRepoBranchesQuery.Data, List<String>, GetGithubRepoBranches.Params>(
    dispatchers = dispatchers,
    mapper = RepoBranchesMapper()
) {

    override fun apiRequest(param: Params): ApolloCall<GithubRepoBranchesQuery.Data> {
        return githubClient.query(
            GithubRepoBranchesQuery(
                name = param.name,
                owner = param.owner,
            ),
        )
    }

    data class Params(
        val name: String,
        val owner: String,
    )
}