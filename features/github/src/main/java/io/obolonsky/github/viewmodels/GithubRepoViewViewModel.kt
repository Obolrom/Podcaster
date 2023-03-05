package io.obolonsky.github.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.core.di.repositories.github.GitHubUserRepo
import io.obolonsky.core.di.utils.NetworkStatus
import io.obolonsky.core.di.utils.NetworkStatusObservable
import io.obolonsky.core.di.utils.reactWith
import io.obolonsky.github.redux.repoview.GithubRepoViewState
import io.obolonsky.github.redux.repoview.RepoViewSideEffects
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.syntax.simple.repeatOnSubscription
import org.orbitmvi.orbit.viewmodel.container
import io.obolonsky.core.R as CoreR

@Suppress("unused_parameter")
class GithubRepoViewViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val githubRepo: GitHubUserRepo,
    private val networkStatusObservable: NetworkStatusObservable,
) : ViewModel(), ContainerHost<GithubRepoViewState, RepoViewSideEffects> {

    override val container: Container<GithubRepoViewState, RepoViewSideEffects> = container(
        GithubRepoViewState(model = null)
    )

    init {
        observeNetworkConnectivity()
    }

    fun toggleRepoStar() = intent {
        val model = state.model
        if (model != null) {
            val action =
                if (model.viewerHasStarred) githubRepo.removeRepoStar(model.id)
                else githubRepo.addRepoStar(model.id)

            action
                .reactWith(
                    onSuccess = { response ->
                        postSideEffect(RepoViewSideEffects.TogglingStarSucceed(CoreR.string.starred))
                        reduce {
                            state.copy(model = state.model?.copy(
                                viewerHasStarred = response.viewerHasStarred,
                                stargazerCount = response.stargazerCount,
                            ))
                        }
                    },
                    onError = {
                        postSideEffect(RepoViewSideEffects.TogglingStarFailed(it))
                    }
                )
                .collect()
        }


    }

    private fun observeNetworkConnectivity() = intent(registerIdling = false) {
        repeatOnSubscription {
            networkStatusObservable.statusFlow
                .filter { status -> status == NetworkStatus.AVAILABLE && state.model == null }
                .onEach {
                    loadRepo()
                }
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