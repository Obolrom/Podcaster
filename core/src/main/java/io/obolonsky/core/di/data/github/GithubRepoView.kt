package io.obolonsky.core.di.data.github

data class GithubRepoView(
    val id: String,
    val repoName: String,
    val owner: String,
    val stargazerCount: Int,
    val forkCount: Int,
    val description: String?,
    val treeEntries: List<RepoTreeEntry>,
    val viewerHasStarred: Boolean,
    val defaultBranchName: String,
    val branches: List<String>? = null,
)

data class RepoTreeEntry(
    val name: String,
    val type: String,
    val mode: Int,
    val treePath: String,
    val lastCommit: LastCommit,
) {

    data class LastCommit(
        val message: String,
        val date: String,
    )
}