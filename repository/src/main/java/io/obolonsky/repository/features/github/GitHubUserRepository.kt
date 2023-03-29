@file:OptIn(FlowPreview::class)

package io.obolonsky.repository.features.github

import dagger.Reusable
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.github.*
import io.obolonsky.core.di.reactWithSuccessOrNull
import io.obolonsky.core.di.repositories.github.GitHubUserRepo
import io.obolonsky.network.apihelpers.github.*
import kotlinx.coroutines.FlowPreview
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
    private val getGithubRepoBranches: GetGithubRepoBranches,
    private val getGithubViewerReposApiHelper: GetGithubViewerReposApiHelper,
) : GitHubUserRepo {

    @Deprecated("do not use rest implementation")
    override suspend fun getUserInformation(): Reaction<GithubUser> {
        return getGithubUserApiHelper.load(Unit)
    }

    override fun getViewerRepos(): Flow<Reaction<List<GithubRepoView>>> {
        return getGithubViewerReposApiHelper.load(Unit)
    }

    override fun getReposBySearchQuery(repoName: String): Flow<Reaction<List<GithubRepository>?>> {
        return getGithubSearchReposApiHelper.load(repoName)
    }

    override fun getRepoBranches(
        repoName: String,
        repoOwner: String
    ): Flow<Reaction<List<String>>> {
        return getGithubRepoBranches.load(GetGithubRepoBranches.Params(repoName, repoOwner))
    }

    // TODO: move to reaction extensions
    inline fun <T, R> Flow<Reaction<T>>.mapReaction(
        crossinline transform: suspend (value: T) -> R
    ): Flow<Reaction<R>> = map {
        when (it) {
            is Reaction.Success -> Reaction.success(transform(it.data))
            is Reaction.Fail -> Reaction.fail(it.error)
        }
    }

    fun <T, R> Flow<Reaction<T>>.flatMapReactionMerge(
        transform: suspend (value: T) -> Flow<Reaction<R>>
    ): Flow<Reaction<R>> = flatMapMerge {
        when (it) {
            is Reaction.Success -> transform(it.data)
            is Reaction.Fail -> flowOf(Reaction.fail(it.error))
        }
    }

    override fun getGithubRepoView(): Flow<Reaction<GithubRepoView>> {
        return getGithubRepoApiHelper.load(Unit)
            .flatMapReactionMerge { repoView ->
                val entriesFlow = repoView.treeEntries
                    .map { entry ->
                        getLastCommitForEntryApiHelper.load(GetLastCommitForEntryApiHelper.Params(
                            name = repoView.repoName,
                            owner = repoView.owner,
                            branchName = repoView.defaultBranchName,
                            treeEntryPath = entry.treePath,
                        ))
                            .mapNotNull { it.reactWithSuccessOrNull() }
                            .map { entry.copy(lastCommit = it) }
                    }
                    .toList()

                val entries = entriesFlow
                    .merge()
                    .toList(ArrayList(repoView.treeEntries.size))

                flowOf(Reaction.success(repoView.copy(treeEntries = entries)))
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