package io.obolonsky.core.di.data.github

data class GithubUserProfile(
    val id: String,
    val login: String,
    val avatarUrl: String,
    val email: String,
    val followers: Int,
    val following: Int,
    val status: Status,
    val contributionChart: ContributionChart? = null,
) {

    data class Status(
        val message: String?,
        val emoji: String?,
    )
}

data class ContributionChart(
    val totalContributionsForLastYear: Int,
    val days: List<GithubDay>,
)

data class GithubDay(
    val contributionCount: Int,
    val color: String,
    val date: String,
)