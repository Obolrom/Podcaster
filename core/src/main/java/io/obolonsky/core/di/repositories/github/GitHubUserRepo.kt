package io.obolonsky.core.di.repositories.github

import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.github.GithubUser
import io.obolonsky.core.di.data.github.GithubUserProfile

interface GitHubUserRepo {

    @Deprecated("do not user rest implementation")
    suspend fun getUserInformation(): Reaction<GithubUser>

    suspend fun getViewerProfile(): Reaction<GithubUserProfile>
}