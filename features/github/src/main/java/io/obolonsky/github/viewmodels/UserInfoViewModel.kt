package io.obolonsky.github.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.core.di.data.github.GithubDay
import io.obolonsky.core.di.utils.reactWith
import io.obolonsky.github.interactors.GitHubProfileInteractor
import io.obolonsky.github.redux.userinfo.UserInfoSideEffects
import io.obolonsky.github.redux.userinfo.UserInfoState
import kotlinx.coroutines.flow.*
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import io.obolonsky.core.R as CoreR

@Suppress("unused_parameter")
class UserInfoViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val gitHubProfileInteractor: GitHubProfileInteractor,
) : ViewModel(), ContainerHost<UserInfoState, UserInfoSideEffects> {

    override val container: Container<UserInfoState, UserInfoSideEffects> = container(
        initialState = UserInfoState(
            isLoading = false,
            user = null,
        ),
    )

    init {
        loadUserInfo()
    }

    @Suppress("unused")
    fun corruptAccessToken() {
        gitHubProfileInteractor.corruptAccessToken()
    }

    fun chartDaySelected(day: GithubDay) = intent {
        postSideEffect(UserInfoSideEffects.ChartDayEvent(day))
    }

    private fun loadUserInfo() = intent {
        reduce {
            state.copy(isLoading = true)
        }

        gitHubProfileInteractor.getCurrentUserProfile()
            .reactWith(
                onSuccess = {
                    reduce { state.copy(isLoading = false, user = it) }
                },
                onError = {
                    reduce { state.copy(isLoading = false, user = null) }
                    postSideEffect(UserInfoSideEffects.ToastEvent(CoreR.string.get_user_info))
                }
            )
            .collect()
    }

    fun logout() = intent {
        val logoutPageIntent = gitHubProfileInteractor.getLogoutIntent()

        postSideEffect(UserInfoSideEffects.LogoutPageEvent(logoutPageIntent))
    }

    fun webLogoutComplete() = intent {
        gitHubProfileInteractor.logout()

        postSideEffect(UserInfoSideEffects.LogoutPageCompletedEvent)
    }

    override fun onCleared() {
        super.onCleared()
        gitHubProfileInteractor.dispose()
    }

    @AssistedFactory
    interface Factory {

        fun create(savedStateHandle: SavedStateHandle): UserInfoViewModel
    }
}