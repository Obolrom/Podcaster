package io.obolonsky.core.di.data.github

data class GithubRepoView(
    val id: String,
    val repoName: String,
    val owner: RepoOwner,
    val stargazerCount: Int,
    val forkCount: Int,
    val description: String?,
    val treeEntries: List<RepoTreeEntry>,
    val viewerHasStarred: Boolean,
    val defaultBranchName: String,
    val branches: List<String>? = null,
    val isFork: Boolean = false,
    val parent: GithubRepoView? = null,
    val visibility: RepoVisibility = RepoVisibility.PUBLIC,
    val updatedAt: String? = null,
    val primaryLanguage: ProgrammingLang? = null,
    val topics: List<Topic> = emptyList(),
    val isInOrganization: Boolean = false,
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

data class RepoOwner(
    val login: String,
    val avatarUrl: String? = null,
)

data class ProgrammingLang(
    val id: String,
    val color: String?,
    val langName: String,
)

data class Topic(
    val name: String,
)