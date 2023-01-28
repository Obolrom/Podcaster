package io.obolonsky.network.mappers.github

import io.obolonsky.core.di.data.github.GithubUser
import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.responses.github.GitHubUserResponse

class GithubUserMapper : Mapper<GitHubUserResponse, GithubUser> {

    override fun map(input: GitHubUserResponse): GithubUser {
        return GithubUser(
            id = requireNotNull(input.id),
            login = requireNotNull(input.login),
            name = input.name,
        )
    }
}