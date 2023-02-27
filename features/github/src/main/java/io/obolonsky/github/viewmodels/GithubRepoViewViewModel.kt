package io.obolonsky.github.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.core.di.repositories.github.GitHubUserRepo
import io.obolonsky.core.di.utils.reactWith
import io.obolonsky.github.redux.repoview.GithubRepoViewState
import io.obolonsky.github.redux.repoview.RepoViewSideEffects
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

@Suppress("unused_parameter")
class GithubRepoViewViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val githubRepo: GitHubUserRepo,
) : ViewModel(), ContainerHost<GithubRepoViewState, RepoViewSideEffects> {

    override val container: Container<GithubRepoViewState, RepoViewSideEffects> = container(
        GithubRepoViewState(model = null)
    )

    init {
        loadRepo()
    }

    fun toggleRepoStar() = intent {
        val model = state.model
        if (model != null) {
            githubRepo.addRepoStar(model.id)
                .reactWith(
                    onSuccess = { response ->
                        reduce {
                            state.copy(model = state.model?.copy(viewerHasStarred = response))
                        }
                    },
                    onError = {
                        postSideEffect(RepoViewSideEffects.TogglingStarFailed(it))
                    }
                )
                .collect()
        }


    }

    private fun loadRepo() = intent {

        githubRepo.getGithubRepoView()
            .reactWith(
                onSuccess = {
                    reduce { state.copy(model = it) }
                },
                onError = { }
            )
            .collect()
    }

    @AssistedFactory
    interface Factory {

        fun create(savedStateHandle: SavedStateHandle): GithubRepoViewViewModel
    }
}