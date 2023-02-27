package io.obolonsky.core.di.data.github

data class GithubRepoStarToggle(
    val viewerHasStarred: Boolean,
    val stargazerCount: Int,
)
