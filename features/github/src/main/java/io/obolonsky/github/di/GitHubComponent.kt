package io.obolonsky.github.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.AuthorizationServiceProvider
import io.obolonsky.core.di.depsproviders.ToolsProvider
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.github.viewmodels.AuthViewModel
import io.obolonsky.github.viewmodels.UserInfoViewModel

@FeatureScope
@Component(
    dependencies = [
        ToolsProvider::class,
        AuthorizationServiceProvider::class,
    ],
    modules = [
        GitHubModule::class,
    ]
)
internal interface GitHubComponent {

    @Component.Factory
    interface Factory {

        fun create(
            toolsProvider: ToolsProvider,
            authorizationServiceProvider: AuthorizationServiceProvider,
        ): GitHubComponent
    }

    fun getAuthViewModelFactory(): AuthViewModel.Factory

    fun getUserInfoViewModelFactory(): UserInfoViewModel.Factory
}