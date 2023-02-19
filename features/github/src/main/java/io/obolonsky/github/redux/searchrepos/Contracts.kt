package io.obolonsky.github.redux.searchrepos

import io.obolonsky.core.di.data.github.GithubRepository

data class SearchReposState(
    val searchResults: List<GithubRepository>?,
)

sealed class SearchReposSideEffects {


}