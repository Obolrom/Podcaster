package io.obolonsky.github.interactors

import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import io.obolonsky.github.AuthRepository
import io.obolonsky.github.RemoteGithubUser
import io.obolonsky.github.UserRepository
import net.openid.appauth.AuthorizationService
import javax.inject.Inject

class GitHubProfileInteractor @Inject constructor(
    private val authRepository: AuthRepository,
    private val authService: AuthorizationService,
    private val userRepository: UserRepository,
) {

    fun corruptAccessToken() {
        authRepository.corruptAccessToken()
    }

    fun getLogoutIntent(): Intent {
        val customTabsIntent = CustomTabsIntent.Builder().build()

        return authService.getEndSessionRequestIntent(
            authRepository.getEndSessionRequest(),
            customTabsIntent
        )
    }

    suspend fun getUserInformation(): RemoteGithubUser {
        return userRepository.getUserInformation()
    }

    fun logout() {
        authRepository.logout()
    }

    fun dispose() {
        authService.dispose()
    }
}