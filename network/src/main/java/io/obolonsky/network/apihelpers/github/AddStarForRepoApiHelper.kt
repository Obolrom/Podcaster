package io.obolonsky.network.apihelpers.github

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.apihelpers.base.SingleFlowApiHelper
import io.obolonsky.network.apihelpers.base.apolloWithReaction
import io.obolonsky.network.github.AddStarForRepoMutation
import io.obolonsky.network.utils.GitHub
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// TODO: refactor to use basic class for mutations
class AddStarForRepoApiHelper @Inject constructor(
    @GitHub private val githubClient: ApolloClient,
    private val dispatchers: CoroutineSchedulers,
) : SingleFlowApiHelper<String, Boolean> {

    override fun load(param: String): Flow<Reaction<Boolean>> {
        return apiRequest(param)
            .toFlow()
            .map {
                it.dataAssertNoErrors.addStar
                    ?.starrable
                    ?.onRepository
                    ?.viewerHasStarred
                    ?: false
            }
            .map<Boolean, Reaction<Boolean>> { Reaction.success(it) }
            .catch { emit(it.apolloWithReaction()) }
            .flowOn(dispatchers.computation)
    }

    private fun apiRequest(param: String): ApolloCall<AddStarForRepoMutation.Data> {
        return githubClient.mutation(AddStarForRepoMutation(repoId = param))
    }
}