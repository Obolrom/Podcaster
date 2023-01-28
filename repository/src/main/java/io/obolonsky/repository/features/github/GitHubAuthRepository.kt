package io.obolonsky.repository.features.github

import dagger.Reusable
import io.obolonsky.core.di.repositories.github.GitHubAuthRepo
import io.obolonsky.network.apihelpers.github.TokenStorage
import io.obolonsky.network.interceptors.github.GithubAppAuth
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.TokenRequest
import timber.log.Timber
import javax.inject.Inject

@Reusable
class GitHubAuthRepository @Inject constructor() : GitHubAuthRepo {

    override fun corruptAccessToken() {
        TokenStorage.accessToken = "fake token"
    }

    override fun logout() {
        TokenStorage.accessToken = null
        TokenStorage.refreshToken = null
        TokenStorage.idToken = null
    }

    override fun getAuthRequest(): AuthorizationRequest {
        return GithubAppAuth.getAuthRequest()
    }

    override fun getEndSessionRequest(): EndSessionRequest {
        return GithubAppAuth.getEndSessionRequest()
    }

    override suspend fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest
    ) {
        val tokens = GithubAppAuth.performTokenRequestSuspend(authService, tokenRequest)
        //обмен кода на токен произошел успешно, сохраняем токены и завершаем авторизацию
        TokenStorage.accessToken = tokens.accessToken
        TokenStorage.refreshToken = tokens.refreshToken
        TokenStorage.idToken = tokens.idToken
        Timber.tag("Oauth").d("6. Tokens accepted:\n access=${tokens.accessToken}\nrefresh=${tokens.refreshToken}\nidToken=${tokens.idToken}")
    }
}