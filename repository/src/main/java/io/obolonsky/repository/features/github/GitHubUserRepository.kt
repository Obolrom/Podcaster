package io.obolonsky.repository.features.github

import dagger.Reusable
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.github.*
import io.obolonsky.core.di.repositories.github.GitHubUserRepo
import io.obolonsky.network.apihelpers.github.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@Reusable
class GitHubUserRepository @Inject constructor(
    private val getGithubUserApiHelper: GetGithubUserApiHelper,
    private val githubUserProfileApiHelper: GetGithubUserProfileApiHelper,
    private val githubUserViewerApiHelper: GetGithubViewerProfileApiHelper,
    private val getGithubSearchReposApiHelper: GetGithubSearchReposApiHelper,
    private val getGithubRepoApiHelper: GetGithubRepoApiHelper,
    private val addStarForRepoApiHelper: AddStarForRepoApiHelper,
    private val removeStarForRepoApiHelper: RemoveStarForRepoApiHelper,
) : GitHubUserRepo {

    @Deprecated("do not use rest implementation")
    override suspend fun getUserInformation(): Reaction<GithubUser> {
        return getGithubUserApiHelper.load(Unit)
    }

    override fun getReposBySearchQuery(repoName: String): Flow<Reaction<List<GithubRepository>>> {
        return getGithubSearchReposApiHelper.load(repoName)
    }

    override fun getGithubRepoView(): Flow<Reaction<GithubRepoView>> {
        return getGithubRepoApiHelper.load(Unit)
    }

    override fun getViewerProfile(): Flow<Reaction<GithubUserProfile>> {
        return githubUserViewerApiHelper.load(Unit)
    }

    override fun addRepoStar(repoId: String): Flow<Reaction<GithubRepoStarToggle>> {
        return addStarForRepoApiHelper.load(repoId)
    }

    override fun removeRepoStar(repoId: String): Flow<Reaction<GithubRepoStarToggle>> {
        return removeStarForRepoApiHelper.load(repoId)
    }
}