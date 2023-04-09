package io.obolonsky.core.di.depsproviders

import net.openid.appauth.AuthorizationService

interface AuthorizationServiceProvider {

    val authorizationService: AuthorizationService
}