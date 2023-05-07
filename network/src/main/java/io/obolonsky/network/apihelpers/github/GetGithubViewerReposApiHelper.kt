package io.obolonsky.network.apihelpers.github

import androidx.annotation.VisibleForTesting
import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import io.obolonsky.core.di.data.github.GithubRepoView
import io.obolonsky.core.di.data.github.SortFilter
import io.obolonsky.core.di.data.github.SortFilter.*
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.apihelpers.base.BaseSingleFlowGraphQlApiHelper
import io.obolonsky.network.github.GithubViewerReposQuery
import io.obolonsky.network.github.type.OrderDirection
import io.obolonsky.network.github.type.RepositoryOrder
import io.obolonsky.network.github.type.RepositoryOrderField
import io.obolonsky.network.mappers.github.ViewerReposMapper
import io.obolonsky.network.utils.GitHub
import javax.inject.Inject

class GetGithubViewerReposApiHelper @Inject constructor(
    @GitHub private val githubClient: ApolloClient,
    dispatchers: CoroutineSchedulers,
) : BaseSingleFlowGraphQlApiHelper<GithubViewerReposQuery.Data, List<GithubRepoView>, GetGithubViewerReposApiHelper.Params>(
    dispatchers = dispatchers,
    mapper = ViewerReposMapper()
) {

    override fun apiRequest(param: Params): ApolloCall<GithubViewerReposQuery.Data> {
        return githubClient.query(GithubViewerReposQuery(
            repositoryOrder = getRepositoryOrder(param)
        ))
    }

    @VisibleForTesting
    fun getRepositoryOrder(param: Params): RepositoryOrder = when (param.sortFilter) {
        LAST_UPDATED -> RepositoryOrder(OrderDirection.DESC, RepositoryOrderField.PUSHED_AT)
        NAME -> RepositoryOrder(OrderDirection.ASC, RepositoryOrderField.NAME)
        STARS -> RepositoryOrder(OrderDirection.DESC, RepositoryOrderField.STARGAZERS)
    }

    data class Params(
        val sortFilter: SortFilter,
    )
}