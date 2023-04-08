package io.obolonsky.network.mappers.github

import io.obolonsky.core.di.data.github.*
import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.github.GetGithubViewerProfileQuery
import io.obolonsky.network.github.GithubRepositoriesSearchQuery
import io.obolonsky.network.github.GithubUserProfileQuery

class GithubSearchReposMapper :
    Mapper<GithubRepositoriesSearchQuery.Data, List<GithubRepoView>?> {

    override fun map(input: GithubRepositoriesSearchQuery.Data): List<GithubRepoView>? {
        return input.search
            .repos
            ?.mapNotNull { it?.repo?.onRepository }
            ?.map { repo ->
                GithubRepoView(
                    id = repo.id,
                    repoName = repo.name,
                    owner = repo.owner.login,
                    stargazerCount = repo.stargazerCount,
                    forkCount = repo.forkCount,
                    description = repo.description,
                    treeEntries = emptyList(),
                    viewerHasStarred = repo.viewerHasStarred,
                    defaultBranchName = "",
                    isFork = repo.isFork,
                    visibility = RepoVisibility.valueOf(repo.visibility.rawValue),
                    updatedAt = repo.updatedAt as String,
                    parent = repo.parent?.let { parentRepo ->
                        GithubRepoView(
                            id = parentRepo.id,
                            repoName = parentRepo.name,
                            owner = parentRepo.owner.login,
                            stargazerCount = -1,
                            forkCount = -1,
                            description = null,
                            treeEntries = emptyList(),
                            viewerHasStarred = false,
                            defaultBranchName = "no info",
                        )
                    },
                    primaryLanguage = repo.primaryLanguage?.let { lang ->
                        ProgrammingLang(
                            id = lang.id,
                            color = lang.color,
                            langName = lang.name,
                        )
                    },
                )
            }
    }
}

class GithubUserProfileMapper : Mapper<GithubUserProfileQuery.Data, GithubUserProfile> {

    override fun map(input: GithubUserProfileQuery.Data): GithubUserProfile {
        return GithubUserProfile(
            id = requireNotNull(input.user?.id),
            login = requireNotNull(input.user?.login),
            avatarUrl = input.user?.avatarUrl.toString(),
            email = requireNotNull(input.user?.email),
            followers = requireNotNull(input.user?.followers?.totalCount),
            following = 0,
            status = GithubUserProfile.Status(
                message = null,
                emoji = null,
            )
        )
    }
}

class GithubViewerProfileMapper : Mapper<GetGithubViewerProfileQuery.Data, GithubUserProfile> {

    override fun map(input: GetGithubViewerProfileQuery.Data): GithubUserProfile {
        val viewer = input.viewer
        val contributionCalendar = viewer.contributionsCollection.contributionCalendar
        val contributions = contributionCalendar.weeks
            .flatMap { it.contributionDays }
            .map {
                GithubDay(
                    contributionCount = it.contributionCount,
                    color = it.color,
                    date = it.date.toString(),
                )
            }

        return GithubUserProfile(
            id = viewer.id,
            login = viewer.login,
            avatarUrl = viewer.avatarUrl.toString(),
            email = viewer.email,
            followers = viewer.followers.totalCount,
            following = viewer.following.totalCount,
            status = GithubUserProfile.Status(
                message = viewer.status?.message,
                emoji = viewer.status?.emoji,
            ),
            contributionChart = ContributionChart(
                totalContributionsForLastYear = contributionCalendar.totalContributions,
                days = contributions,
            )
        )
    }
}