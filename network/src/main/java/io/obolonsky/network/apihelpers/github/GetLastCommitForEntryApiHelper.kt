package io.obolonsky.network.apihelpers.github

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import io.obolonsky.core.di.data.github.RepoTreeEntry
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.apihelpers.base.BaseSingleFlowGraphQlApiHelper
import io.obolonsky.network.github.GithubLastCommitQuery
import io.obolonsky.network.mappers.github.LastCommitForEntryMapper
import io.obolonsky.network.utils.GitHub
import javax.inject.Inject

class GetLastCommitForEntryApiHelper @Inject constructor(
    @GitHub private val githubClient: ApolloClient,
    dispatchers: CoroutineSchedulers,
) : BaseSingleFlowGraphQlApiHelper<
        GithubLastCommitQuery.Data,
        RepoTreeEntry.LastCommit,
        GetLastCommitForEntryApiHelper.Params>
    (
    dispatchers = dispatchers,
    mapper = LastCommitForEntryMapper(),
) {

    override fun apiRequest(
        param: Params
    ): ApolloCall<GithubLastCommitQuery.Data> {
        return githubClient.query(GithubLastCommitQuery(
            name = param.name,
            owner = param.owner,
            branchName = param.branchName,
            treeEntryPath = param.treeEntryPath
        ))
    }

    data class Params(
        val name: String,
        val owner: String,
        val branchName: String,
        val treeEntryPath: String,
    )
}