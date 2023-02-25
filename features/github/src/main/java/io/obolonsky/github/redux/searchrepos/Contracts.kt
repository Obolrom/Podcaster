package io.obolonsky.github.redux.searchrepos

import io.obolonsky.core.di.data.github.GithubRepository
import io.obolonsky.core.di.Error

data class SearchReposState(
    val searchResults: List<GithubRepository>?,
)

sealed class SearchReposSideEffects {

    data class SearchError(val error: Error) : SearchReposSideEffects()
}