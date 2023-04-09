package io.obolonsky.network.api

import io.obolonsky.network.responses.github.GitHubUserResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface GithubApi {

    @GET("user")
    fun getCurrentUser(): Single<GitHubUserResponse>
}