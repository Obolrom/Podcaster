package io.obolonsky.github.viewmodels

import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.core.di.data.github.GithubUserProfile
import io.obolonsky.core.di.reactWith
import io.obolonsky.github.interactors.GitHubProfileInteractor
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import io.obolonsky.core.R as CoreR

@Suppress("unused_parameter")
class UserInfoViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val gitHubProfileInteractor: GitHubProfileInteractor,
) : ViewModel() {

    private val loadingMutableStateFlow = MutableStateFlow(false)
    private val userInfoMutableStateFlow = MutableStateFlow<GithubUserProfile?>(null)
    private val toastEventChannel = Channel<Int>(Channel.BUFFERED)
    private val logoutPageEventChannel = Channel<Intent>(Channel.BUFFERED)
    private val logoutCompletedEventChannel = Channel<Unit>(Channel.BUFFERED)

    val loadingFlow: Flow<Boolean>
        get() = loadingMutableStateFlow.asStateFlow()

    val userInfoFlow: Flow<GithubUserProfile?>
        get() = userInfoMutableStateFlow.asStateFlow()

    val toastFlow: Flow<Int>
        get() = toastEventChannel.receiveAsFlow()

    val logoutPageFlow: Flow<Intent>
        get() = logoutPageEventChannel.receiveAsFlow()

    val logoutCompletedFlow: Flow<Unit>
        get() = logoutCompletedEventChannel.receiveAsFlow()

    fun corruptAccessToken() {
        gitHubProfileInteractor.corruptAccessToken()
    }

    fun loadUserInfo() {
        viewModelScope.launch {
            loadingMutableStateFlow.value = true
            gitHubProfileInteractor.getCurrentUserProfile()
                .reactWith(
                    onSuccess = {
                        userInfoMutableStateFlow.value = it
                        loadingMutableStateFlow.value = false
                    },
                    onError = {
                        loadingMutableStateFlow.value = false
                        userInfoMutableStateFlow.value = null
                        toastEventChannel.trySendBlocking(CoreR.string.get_user_error)
                    }
                )
        }
    }

    fun logout() {
        val logoutPageIntent = gitHubProfileInteractor.getLogoutIntent()

        logoutPageEventChannel.trySendBlocking(logoutPageIntent)
    }

    fun webLogoutComplete() {
        gitHubProfileInteractor.logout()
        logoutCompletedEventChannel.trySendBlocking(Unit)
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