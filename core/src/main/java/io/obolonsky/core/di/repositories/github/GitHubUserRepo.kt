package io.obolonsky.core.di.repositories.github

import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.github.GithubUser

interface GitHubUserRepo {

    suspend fun getUserInformation(): Reaction<GithubUser>
}