package io.obolonsky.network.mappers.github

import io.obolonsky.core.di.data.github.GithubUserProfile
import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.github.GetGithubViewerProfileQuery
import io.obolonsky.network.github.GithubUserProfileQuery

class GithubUserProfileMapper : Mapper<GithubUserProfileQuery.Data, GithubUserProfile> {

    override fun map(input: GithubUserProfileQuery.Data): GithubUserProfile {
        return GithubUserProfile(
            id = requireNotNull(input.user?.id),
            login = requireNotNull(input.user?.login),
            avatarUrl = input.user?.avatarUrl.toString(),
            email = requireNotNull(input.user?.email),
        )
    }
}

class GithubViewerProfileMapper : Mapper<GetGithubViewerProfileQuery.Data, GithubUserProfile> {

    override fun map(input: GetGithubViewerProfileQuery.Data): GithubUserProfile {
        return GithubUserProfile(
            id = requireNotNull(input.viewer.id),
            login = requireNotNull(input.viewer.login),
            avatarUrl = input.viewer.avatarUrl.toString(),
            email = requireNotNull(input.viewer.email),
        )
    }
}