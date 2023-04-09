package io.obolonsky.core.di.data.github

data class GithubRepository(
    val name: String,
    val nameWithOwner: String,
    val stargazerCount: Int,
)
