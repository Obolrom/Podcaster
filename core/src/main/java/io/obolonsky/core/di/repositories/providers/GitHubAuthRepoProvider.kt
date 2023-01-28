package io.obolonsky.core.di.repositories.providers

import io.obolonsky.core.di.repositories.github.GitHubAuthRepo

interface GitHubAuthRepoProvider {

    val gitHubAuthRepo: GitHubAuthRepo
}