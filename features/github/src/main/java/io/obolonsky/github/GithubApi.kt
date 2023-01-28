package io.obolonsky.github

import retrofit2.http.GET

interface GithubApi {

    @GET("user")
    suspend fun getCurrentUser(): RemoteGithubUser

}