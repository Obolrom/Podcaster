package io.obolonsky.github.redux.repoview

import io.obolonsky.core.di.data.github.GithubRepoView
import io.obolonsky.core.di.Error

data class GithubRepoViewState(
    val model: GithubRepoView?,
)

sealed class RepoViewSideEffects {

    data class TogglingStarFailed(val error: Error) : RepoViewSideEffects()
}