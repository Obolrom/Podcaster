package io.obolonsky.core.di.data.github

data class GithubUser(
    val id: Long,
    val login: String,
    val name: String?,
)
