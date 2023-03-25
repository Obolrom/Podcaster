package io.obolonsky.core.di.data.github

data class GithubUserProfile(
    val id: String,
    val login: String,
    val avatarUrl: String,
    val email: String,
    val followers: Int,
    val following: Int,
    val status: Status,
) {

    data class Status(
        val message: String?,
        val emoji: String?,
    )
}