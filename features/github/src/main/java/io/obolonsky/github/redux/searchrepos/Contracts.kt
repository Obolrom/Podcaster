package io.obolonsky.github.redux.searchrepos

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.data.github.GithubRepoView

data class SearchReposState(
    val searchResults: List<GithubRepoView>?,
)

sealed class SearchReposSideEffects {

    data class SearchError(val error: Error) : SearchReposSideEffects()
}