@file:OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)

package io.obolonsky.github.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.core.di.utils.reactWith
import io.obolonsky.github.interactors.SearchReposInteractor
import io.obolonsky.github.redux.searchrepos.SearchReposSideEffects
import io.obolonsky.github.redux.searchrepos.SearchReposState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.syntax.simple.repeatOnSubscription
import org.orbitmvi.orbit.viewmodel.container

@Suppress("unused_parameter")
class SearchReposViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val searchReposInteractor: SearchReposInteractor,
) : ViewModel(), ContainerHost<SearchReposState, SearchReposSideEffects> {

    override val container: Container<SearchReposState, SearchReposSideEffects> = container(
        initialState = SearchReposState(searchResults = null)
    )

    init {
        search()
    }

    val repoNameSearchQuery = MutableStateFlow("")

    private fun search() = intent(registerIdling = false) {
        repeatOnSubscription {
            repoNameSearchQuery
                .drop(1)
                .map { it.trim() }
                .debounce(400)
                .filter { query -> query.isNotEmpty() }
                .flatMapLatest { query ->
                    searchReposInteractor.getReposBySearchQuery(query)
                }
                .reactWith(
                    onSuccess = {
                        reduce { state.copy(searchResults = it) }
                    },
                    onError = {
                        postSideEffect(SearchReposSideEffects.SearchError(it))
                    }
                )
                .collect()
        }
    }

    @AssistedFactory
    interface Factory {

        fun create(savedStateHandle: SavedStateHandle): SearchReposViewModel
    }
}