package io.obolonsky.github

import javax.inject.Inject

class UserRepository @Inject constructor(
    private val githubApi: GithubApi,
) {

    suspend fun getUserInformation(): RemoteGithubUser {
        return githubApi.getCurrentUser()
    }
}