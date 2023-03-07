package io.obolonsky.github.redux.repoview

import androidx.annotation.StringRes
import io.obolonsky.core.di.data.github.GithubRepoView
import io.obolonsky.core.di.Error

data class GithubRepoViewState(
    val model: GithubRepoView?,
    val shouldShowRepoTree: Boolean,
)

sealed class RepoViewSideEffects {

    data class TogglingStarFailed(val error: Error) : RepoViewSideEffects()

    data class TogglingStarSucceed(@StringRes val messageResId: Int) : RepoViewSideEffects()
}