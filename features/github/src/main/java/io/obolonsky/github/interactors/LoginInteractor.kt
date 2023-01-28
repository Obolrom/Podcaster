package io.obolonsky.github.interactors

import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import io.obolonsky.github.AuthRepository
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest
import javax.inject.Inject

class LoginInteractor @Inject constructor(
    private val authRepository: AuthRepository,
    private val authService: AuthorizationService,
) {

    suspend fun performTokenRequest(tokenRequest: TokenRequest) {
        authRepository.performTokenRequest(
            authService = authService,
            tokenRequest = tokenRequest
        )
    }

    fun getAuthRequest(): AuthorizationRequest {
        return authRepository.getAuthRequest()
    }

    fun getAuthorizationRequestIntent(
        authRequest: AuthorizationRequest,
        customTabsIntent: CustomTabsIntent,
    ): Intent {
        return authService.getAuthorizationRequestIntent(
            authRequest,
            customTabsIntent
        )
    }

    fun dispose() {
        authService.dispose()
    }
}