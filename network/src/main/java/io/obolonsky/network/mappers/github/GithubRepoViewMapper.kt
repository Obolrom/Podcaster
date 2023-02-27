package io.obolonsky.network.mappers.github

import io.obolonsky.core.di.data.github.GithubRepoView
import io.obolonsky.core.di.data.github.RepoTreeEntry
import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.github.GithubRepoQuery

class GithubRepoViewMapper : Mapper<GithubRepoQuery.Data, GithubRepoView> {

    override fun map(input: GithubRepoQuery.Data): GithubRepoView {
        val repo = requireNotNull(input.repository)
        val treeEntries = repo.repoTree
            ?.onTree
            ?.entries
            ?.map {
                RepoTreeEntry(name = it.name, type = it.type, mode = it.mode)
            }
            .orEmpty()

        return GithubRepoView(
            id = repo.id,
            repoName = repo.name,
            owner = repo.owner.login,
            stargazerCount = repo.stargazerCount,
            forkCount = repo.forkCount,
            description = repo.description,
            treeEntries = treeEntries,
            viewerHasStarred = repo.viewerHasStarred,
        )
    }
}