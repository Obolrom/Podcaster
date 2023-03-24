package io.obolonsky.network.mappers.github

import io.obolonsky.core.di.data.github.GithubRepoView
import io.obolonsky.core.di.data.github.RepoTreeEntry
import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.github.GithubLastCommitQuery
import io.obolonsky.network.github.GithubRepoBranchesQuery
import io.obolonsky.network.github.GithubRepoQuery
import java.text.SimpleDateFormat
import java.util.*

class GithubRepoViewMapper : Mapper<GithubRepoQuery.Data, GithubRepoView> {

    override fun map(input: GithubRepoQuery.Data): GithubRepoView {
        val repo = requireNotNull(input.repository)
        val treeEntries = repo.repoTree
            ?.onTree
            ?.entries
            ?.map {
                RepoTreeEntry(
                    name = it.name,
                    type = it.type,
                    mode = it.mode,
                    treePath = it.treePath ?: it.name,
                    lastCommit = parseLastCommit(it),
                )
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
            defaultBranchName = repo.defaultBranchRef?.name.orEmpty(),
        )
    }

    private fun parseLastCommit(entry: GithubRepoQuery.Entry): RepoTreeEntry.LastCommit {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)

        val node = entry.repository
            .defaultBranchRef
            ?.target
            ?.onCommit
            ?.history
            ?.edges
            ?.firstOrNull()
            ?.node

        return if (node != null) RepoTreeEntry.LastCommit(
            message = node.message,
            date = formatter.format(dateFormat.parse(node.authoredDate.toString()) ?: Date()),
        )
        else RepoTreeEntry.LastCommit("", "")
    }
}

class LastCommitForEntryMapper : Mapper<GithubLastCommitQuery.Data, RepoTreeEntry.LastCommit> {

    override fun map(input: GithubLastCommitQuery.Data): RepoTreeEntry.LastCommit {
        val node = requireNotNull(
            input.repository?.ref?.target?.onCommit?.history?.edges?.firstOrNull()?.node
        )

        return parseLastCommit(node)
    }

    private fun parseLastCommit(node: GithubLastCommitQuery.Node): RepoTreeEntry.LastCommit {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)

        return RepoTreeEntry.LastCommit(
            message = node.message,
            date = formatter.format(dateFormat.parse(node.authoredDate.toString()) ?: Date()),
        )
    }
}

class RepoBranchesMapper : Mapper<GithubRepoBranchesQuery.Data, List<String>> {

    override fun map(input: GithubRepoBranchesQuery.Data): List<String> {
        val branches = requireNotNull(input.repository?.refs?.nodes)

        return branches.mapNotNull {
            it?.name
        }
    }
}