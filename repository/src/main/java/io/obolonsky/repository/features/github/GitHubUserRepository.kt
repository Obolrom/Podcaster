package io.obolonsky.repository.features.github

import dagger.Reusable
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.github.*
import io.obolonsky.core.di.reactWithSuccessOrNull
import io.obolonsky.core.di.repositories.github.GitHubUserRepo
import io.obolonsky.network.apihelpers.github.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@Reusable
class GitHubUserRepository @Inject constructor(
    private val getGithubUserApiHelper: GetGithubUserApiHelper,
    private val githubUserProfileApiHelper: GetGithubUserProfileApiHelper,
    private val githubUserViewerApiHelper: GetGithubViewerProfileApiHelper,
    private val getGithubSearchReposApiHelper: GetGithubSearchReposApiHelper,
    private val getGithubRepoApiHelper: GetGithubRepoApiHelper,
    private val getLastCommitForEntryApiHelper: GetLastCommitForEntryApiHelper,
    private val addStarForRepoApiHelper: AddStarForRepoApiHelper,
    private val removeStarForRepoApiHelper: RemoveStarForRepoApiHelper,
) : GitHubUserRepo {

    @Deprecated("do not use rest implementation")
    override suspend fun getUserInformation(): Reaction<GithubUser> {
        return getGithubUserApiHelper.load(Unit)
    }

    override fun getReposBySearchQuery(repoName: String): Flow<Reaction<List<GithubRepository>?>> {
        return getGithubSearchReposApiHelper.load(repoName)
    }

    // TODO: move to reaction extensions
    inline fun <T, R> Flow<Reaction<T>>.mapReaction(
        crossinline transform: suspend (value: T) -> R
    ): Flow<Reaction<R>> = transform { value ->
        return@transform when (value) {
            is Reaction.Success -> emit(Reaction.success(transform(value.data)))
            is Reaction.Fail -> emit(Reaction.fail(value.error))
        }
    }

    override fun getGithubRepoView(): Flow<Reaction<GithubRepoView>> {
        return getGithubRepoApiHelper.load(Unit)
            .mapReaction { success ->
                val newEntries = success.treeEntries
                    .map { entry ->
                        val lastCommit = getLastCommitForEntryApiHelper.load(
                            GetLastCommitForEntryApiHelper.Params(
                                name = success.repoName,
                                owner = success.owner,
                                branchName = success.defaultBranchName,
                                treeEntryPath = entry.treePath,
                            )
                        ).first().reactWithSuccessOrNull()!!
                        entry.copy(lastCommit = lastCommit)
                    }
                success.copy(treeEntries = newEntries)
            }
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