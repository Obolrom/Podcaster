package io.obolonsky.core.di.data.github

data class GithubUserProfile(
    val id: String,
    val login: String,
    val avatarUrl: String,
    val email: String,
)