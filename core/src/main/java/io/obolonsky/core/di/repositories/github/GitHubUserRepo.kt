package io.obolonsky.core.di.repositories.github

import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.github.GithubRepoView
import io.obolonsky.core.di.data.github.GithubRepository
import io.obolonsky.core.di.data.github.GithubUser
import io.obolonsky.core.di.data.github.GithubUserProfile
import kotlinx.coroutines.flow.Flow

interface GitHubUserRepo {

    @Deprecated("do not user rest implementation")
    suspend fun getUserInformation(): Reaction<GithubUser>

    /**
     * Returns SINGLE value
     */
    fun getViewerProfile(): Flow<Reaction<GithubUserProfile>>

    /**
     * Returns SINGLE value
     */
    fun getGithubRepoView(): Flow<Reaction<GithubRepoView>>

    fun getReposBySearchQuery(repoName: String): Flow<Reaction<List<GithubRepository>>>

    /**
     * Returns SINGLE value
     */
    fun addRepoStar(repoId: String): Flow<Reaction<Boolean>>

    /**
     * Returns SINGLE value
     */
    fun removeRepoStar(repoId: String): Flow<Reaction<Boolean>>
}