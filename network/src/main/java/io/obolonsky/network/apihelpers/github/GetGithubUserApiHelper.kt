package io.obolonsky.network.apihelpers.github

import io.obolonsky.core.di.data.github.GithubUser
import io.obolonsky.network.api.GithubApi
import io.obolonsky.network.apihelpers.base.RxApiHelper
import io.obolonsky.network.mappers.github.GithubUserMapper
import io.obolonsky.network.responses.github.GitHubUserResponse
import io.obolonsky.network.utils.RxSchedulers
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetGithubUserApiHelper @Inject constructor(
    private val githubApi: GithubApi,
    rxSchedulers: RxSchedulers,
) : RxApiHelper<GitHubUserResponse, GithubUser, Unit>(
    rxSchedulers = rxSchedulers,
    mapper = GithubUserMapper(),
) {

    override fun apiRequest(param: Unit): Single<GitHubUserResponse> {
        return githubApi.getCurrentUser()
    }
}