package io.obolonsky.github.interactors

import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.github.GithubUser
import io.obolonsky.core.di.data.github.GithubUserProfile
import io.obolonsky.core.di.repositories.github.GitHubAuthRepo
import io.obolonsky.core.di.repositories.github.GitHubUserRepo
import io.obolonsky.core.di.scopes.FeatureScope
import net.openid.appauth.AuthorizationService
import javax.inject.Inject

@FeatureScope
class GitHubProfileInteractor @Inject constructor(
    private val authRepository: GitHubAuthRepo,
    private val authService: AuthorizationService,
    private val userRepository: GitHubUserRepo,
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

    suspend fun getUserInformation(): Reaction<GithubUser> {
        return userRepository.getUserInformation()
    }

    suspend fun getCurrentUserProfile(): Reaction<GithubUserProfile> {
        return userRepository.getViewerProfile()
    }

    fun logout() {
        authRepository.logout()
    }

    fun dispose() {
        authService.dispose()
    }
}