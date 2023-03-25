package io.obolonsky.network.mappers.github

import io.obolonsky.core.di.data.github.GithubRepository
import io.obolonsky.core.di.data.github.GithubUserProfile
import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.github.GetGithubViewerProfileQuery
import io.obolonsky.network.github.GithubRepositoriesSearchQuery
import io.obolonsky.network.github.GithubUserProfileQuery

class GithubSearchReposMapper :
    Mapper<GithubRepositoriesSearchQuery.Data, List<GithubRepository>?> {

    override fun map(input: GithubRepositoriesSearchQuery.Data): List<GithubRepository>? {
        return input.search
            .repos
            ?.mapNotNull { it?.repo?.onRepository }
            ?.map { repo ->
                GithubRepository(
                    name = repo.name,
                    nameWithOwner = repo.nameWithOwner,
                    stargazerCount = repo.stargazerCount,
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
            )
        )
    }
}