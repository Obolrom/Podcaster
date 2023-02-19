package io.obolonsky.core.di.repositories.github

import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.github.GithubRepository
import io.obolonsky.core.di.data.github.GithubUser
import io.obolonsky.core.di.data.github.GithubUserProfile
import kotlinx.coroutines.flow.Flow

interface GitHubUserRepo {

    @Deprecated("do not user rest implementation")
    suspend fun getUserInformation(): Reaction<GithubUser>

    suspend fun getViewerProfile(): Reaction<GithubUserProfile>

    fun getReposBySearchQuery(repoName: String): Flow<Reaction<List<GithubRepository>>>
}