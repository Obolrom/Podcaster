package io.obolonsky.network.apihelpers.github

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import io.obolonsky.core.di.data.github.GithubUserProfile
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.apihelpers.base.GraphQlApiHelper
import io.obolonsky.network.github.GithubUserProfileQuery
import io.obolonsky.network.mappers.github.GithubUserProfileMapper
import io.obolonsky.network.utils.GitHub
import javax.inject.Inject

class GetGithubUserProfileApiHelper @Inject constructor(
    @GitHub private val githubClient: ApolloClient,
    dispatchers: CoroutineSchedulers,
) : GraphQlApiHelper<GithubUserProfileQuery.Data, GithubUserProfile, String>(
    dispatchers = dispatchers,
    mapper = GithubUserProfileMapper()
) {

    override fun apiRequest(param: String): ApolloCall<GithubUserProfileQuery.Data> {
        return githubClient.query(GithubUserProfileQuery(param))
    }
}