package io.obolonsky.network.mappers.github

import io.obolonsky.core.di.data.github.*
import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.github.GithubLastCommitQuery
import io.obolonsky.network.github.GithubRepoBranchesQuery
import io.obolonsky.network.github.GithubRepoQuery
import io.obolonsky.network.github.GithubViewerReposQuery
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
            owner = RepoOwner(
                login = repo.owner.login,
            ),
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

class ViewerReposMapper : Mapper<GithubViewerReposQuery.Data, List<GithubRepoView>> {

    override fun map(input: GithubViewerReposQuery.Data): List<GithubRepoView> {
        return input.viewer
            .repositories
            .edges
            ?.mapNotNull { it?.node }
            ?.map {
                GithubRepoView(
                    id = it.id,
                    repoName = it.name,
                    owner = RepoOwner(
                        login = it.owner.login,
                        avatarUrl = it.owner.avatarUrl as String,
                    ),
                    stargazerCount = it.stargazerCount,
                    forkCount = it.forkCount,
                    description = it.description,
                    treeEntries = emptyList(),
                    viewerHasStarred = it.viewerHasStarred,
                    defaultBranchName = "no info",
                    branches = null,
                    isFork = it.isFork,
                    parent = it.parent?.let { parentRepo ->
                        GithubRepoView(
                            id = parentRepo.id,
                            repoName = parentRepo.name,
                            owner = RepoOwner(
                                login = parentRepo.owner.login,
                            ),
                            stargazerCount = -1,
                            forkCount = -1,
                            description = null,
                            treeEntries = emptyList(),
                            viewerHasStarred = false,
                            defaultBranchName = "no info",
                        )
                    },
                    visibility = RepoVisibility.valueOf(it.visibility.rawValue),
                    updatedAt = it.updatedAt as String,
                    primaryLanguage = it.primaryLanguage?.let { lang ->
                        ProgrammingLang(
                            id = lang.id,
                            color = lang.color,
                            langName = lang.name,
                        )
                    },
                    topics = it.repositoryTopics
                        .edges
                        ?.mapNotNull { edge -> edge?.node?.topic }
                        ?.map { topic -> Topic(topic.name) }
                        .orEmpty(),
                )
            }
            .orEmpty()
    }
}