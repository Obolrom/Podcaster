package io.obolonsky.core.di.repositories.providers

import io.obolonsky.core.di.repositories.github.GitHubUserRepo

interface GitHubUserRepoProvider {

    val gitHubUserRepo: GitHubUserRepo
}