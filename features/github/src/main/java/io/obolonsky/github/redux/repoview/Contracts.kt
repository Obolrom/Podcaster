package io.obolonsky.github.redux.repoview

import io.obolonsky.core.di.data.github.GithubRepoView

data class GithubRepoViewState(
    val model: GithubRepoView?,
)

sealed class RepoViewSideEffects