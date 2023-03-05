package io.obolonsky.github.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.AuthorizationServiceProvider
import io.obolonsky.core.di.depsproviders.NetworkStatusObservableProvider
import io.obolonsky.core.di.depsproviders.ToolsProvider
import io.obolonsky.core.di.repositories.providers.GitHubAuthRepoProvider
import io.obolonsky.core.di.repositories.providers.GitHubUserRepoProvider
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.github.viewmodels.AuthViewModel
import io.obolonsky.github.viewmodels.GithubRepoViewViewModel
import io.obolonsky.github.viewmodels.SearchReposViewModel
import io.obolonsky.github.viewmodels.UserInfoViewModel

@FeatureScope
@Component(
    dependencies = [
        ToolsProvider::class,
        AuthorizationServiceProvider::class,
        GitHubAuthRepoProvider::class,
        GitHubUserRepoProvider::class,
        NetworkStatusObservableProvider::class,
    ]
)
internal interface GitHubComponent {

    @Component.Factory
    interface Factory {

        fun create(
            toolsProvider: ToolsProvider,
            authorizationServiceProvider: AuthorizationServiceProvider,
            gitHubAuthRepoProvider: GitHubAuthRepoProvider,
            gitHubUserRepoProvider: GitHubUserRepoProvider,
            networkStatusObservableProvider: NetworkStatusObservableProvider,
        ): GitHubComponent
    }

    fun getAuthViewModelFactory(): AuthViewModel.Factory

    fun getUserInfoViewModelFactory(): UserInfoViewModel.Factory

    fun getSearchReposViewModelFactory(): SearchReposViewModel.Factory

    fun getGithubRepoViewViewModelFactory(): GithubRepoViewViewModel.Factory
}