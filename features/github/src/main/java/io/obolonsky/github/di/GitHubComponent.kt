package io.obolonsky.github.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.ToolsProvider
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.github.viewmodels.AuthViewModel
import io.obolonsky.github.viewmodels.UserInfoViewModel

@FeatureScope
@Component(
    dependencies = [
        ToolsProvider::class,
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
        ): GitHubComponent
    }

    fun getAuthViewModelFactory(): AuthViewModel.Factory

    fun getUserInfoViewModelFactory(): UserInfoViewModel.Factory

    companion object {
        fun create(
            toolsProvider: ToolsProvider,
        ): GitHubComponent {
            return DaggerGitHubComponent.factory()
                .create(
                    toolsProvider = toolsProvider,
                )
        }
    }
}