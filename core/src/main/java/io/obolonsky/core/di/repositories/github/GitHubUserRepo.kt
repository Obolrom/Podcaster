package io.obolonsky.core.di.repositories.github

import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.github.*
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
    fun getGithubRepoView(owner: String, repo: String): Flow<Reaction<GithubRepoView>>

    fun getReposBySearchQuery(repoName: String): Flow<Reaction<List<GithubRepository>?>>

    /**
     * Returns SINGLE value
     */
    fun addRepoStar(repoId: String): Flow<Reaction<GithubRepoStarToggle>>

    /**
     * Returns SINGLE value
     */
    fun removeRepoStar(repoId: String): Flow<Reaction<GithubRepoStarToggle>>

    /**
     * Returns SINGLE value
     */
    fun getRepoBranches(repoName: String, repoOwner: String): Flow<Reaction<List<String>>>

    /**
     * Returns SINGLE value
     */
    fun getViewerRepos(): Flow<Reaction<List<GithubRepoView>>>
}