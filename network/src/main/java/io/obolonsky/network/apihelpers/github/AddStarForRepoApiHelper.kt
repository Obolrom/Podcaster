package io.obolonsky.network.apihelpers.github

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import io.obolonsky.core.di.data.github.GithubRepoStarToggle
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.apihelpers.base.BaseMutationGraphQlApiHelper
import io.obolonsky.network.github.AddStarForRepoMutation
import io.obolonsky.network.mappers.github.GithubAddStarForRepoMapper
import io.obolonsky.network.utils.GitHub
import javax.inject.Inject

class AddStarForRepoApiHelper @Inject constructor(
    @GitHub private val githubClient: ApolloClient,
    dispatchers: CoroutineSchedulers,
) : BaseMutationGraphQlApiHelper<AddStarForRepoMutation.Data, GithubRepoStarToggle, String>(
    dispatchers = dispatchers,
    mapper = GithubAddStarForRepoMapper(),
) {

    override fun apiRequest(param: String): ApolloCall<AddStarForRepoMutation.Data> {
        return githubClient.mutation(AddStarForRepoMutation(repoId = param))
    }
}