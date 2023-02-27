package io.obolonsky.network.apihelpers.github

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.apihelpers.base.BaseMutationGraphQlApiHelper
import io.obolonsky.network.github.RemoveStarForRepoMutation
import io.obolonsky.network.mappers.github.GithubRemoveStarForRepoMapper
import io.obolonsky.network.utils.GitHub
import javax.inject.Inject

class RemoveStarForRepoApiHelper @Inject constructor(
    @GitHub private val githubClient: ApolloClient,
    dispatchers: CoroutineSchedulers,
) : BaseMutationGraphQlApiHelper<RemoveStarForRepoMutation.Data, Boolean, String>(
    dispatchers = dispatchers,
    mapper = GithubRemoveStarForRepoMapper(),
) {

    override fun apiRequest(param: String): ApolloCall<RemoveStarForRepoMutation.Data> {
        return githubClient.mutation(RemoveStarForRepoMutation(repoId = param))
    }
}