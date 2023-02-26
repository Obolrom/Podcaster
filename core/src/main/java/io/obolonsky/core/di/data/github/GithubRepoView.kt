package io.obolonsky.core.di.data.github

data class GithubRepoView(
    val id: String,
    val repoName: String,
    val owner: String,
    val stargazerCount: Int,
    val forkCount: Int,
    val description: String?,
    val treeEntries: List<RepoTreeEntry>,
)

data class RepoTreeEntry(
    val name: String,
    val type: String,
    val mode: Int,
)