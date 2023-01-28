package io.obolonsky.github.viewmodels

import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.github.AuthRepository
import io.obolonsky.github.RemoteGithubUser
import io.obolonsky.github.UserRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationService
import io.obolonsky.core.R as CoreR

@Suppress("unused_parameter")
class UserInfoViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val authService: AuthorizationService,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val loadingMutableStateFlow = MutableStateFlow(false)
    private val userInfoMutableStateFlow = MutableStateFlow<RemoteGithubUser?>(null)
    private val toastEventChannel = Channel<Int>(Channel.BUFFERED)
    private val logoutPageEventChannel = Channel<Intent>(Channel.BUFFERED)
    private val logoutCompletedEventChannel = Channel<Unit>(Channel.BUFFERED)

    val loadingFlow: Flow<Boolean>
        get() = loadingMutableStateFlow.asStateFlow()

    val userInfoFlow: Flow<RemoteGithubUser?>
        get() = userInfoMutableStateFlow.asStateFlow()

    val toastFlow: Flow<Int>
        get() = toastEventChannel.receiveAsFlow()

    val logoutPageFlow: Flow<Intent>
        get() = logoutPageEventChannel.receiveAsFlow()

    val logoutCompletedFlow: Flow<Unit>
        get() = logoutCompletedEventChannel.receiveAsFlow()

    fun corruptAccessToken() {
        authRepository.corruptAccessToken()
    }

    fun loadUserInfo() {
        viewModelScope.launch {
            loadingMutableStateFlow.value = true
            runCatching {
                userRepository.getUserInformation()
            }.onSuccess {
                userInfoMutableStateFlow.value = it
                loadingMutableStateFlow.value = false
            }.onFailure {
                loadingMutableStateFlow.value = false
                userInfoMutableStateFlow.value = null
                toastEventChannel.trySendBlocking(CoreR.string.get_user_error)
            }
        }
    }

    fun logout() {
        val customTabsIntent = CustomTabsIntent.Builder().build()

        val logoutPageIntent = authService.getEndSessionRequestIntent(
            authRepository.getEndSessionRequest(),
            customTabsIntent
        )

        logoutPageEventChannel.trySendBlocking(logoutPageIntent)
    }

    fun webLogoutComplete() {
        authRepository.logout()
        logoutCompletedEventChannel.trySendBlocking(Unit)
    }

    override fun onCleared() {
        super.onCleared()
        authService.dispose()
    }

    @AssistedFactory
    interface Factory {

        fun create(savedStateHandle: SavedStateHandle): UserInfoViewModel
    }
}