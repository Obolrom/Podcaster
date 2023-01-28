package io.obolonsky.repository.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import net.openid.appauth.AuthorizationService

@Module
internal class RepoModule {

    @Provides
    fun provideAuthorizationService(context: Context): AuthorizationService {
        return AuthorizationService(context)
    }
}