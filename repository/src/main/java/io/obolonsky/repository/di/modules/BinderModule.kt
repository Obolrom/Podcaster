package io.obolonsky.repository.di.modules

import dagger.Binds
import dagger.Module
import io.obolonsky.core.di.repositories.*
import io.obolonsky.core.di.repositories.github.GitHubAuthRepo
import io.obolonsky.core.di.repositories.github.GitHubUserRepo
import io.obolonsky.repository.*
import io.obolonsky.repository.features.github.GitHubAuthRepository
import io.obolonsky.repository.features.github.GitHubUserRepository

@Module
interface BinderModule {

    @Binds
    fun bindShazamRepository(
        repo: ShazamRepository
    ): ShazamRepo

    @Binds
    fun bindFeatureTogglesRepository(
        repo: FeatureTogglesRepository
    ): FeatureTogglesRepo

    @Binds
    fun bindSpaceXRepository(
        repo: SpaceXRepository
    ): SpaceXRepo

    @Binds
    fun bindNasaRepository(
        repo: NasaRepository
    ): NasaRepo

    @Binds
    fun bindBanksRepository(
        repo: BanksRepository
    ): BanksRepo

    @Binds
    fun bindDownloadsRepository(
        repo: DownloadsRepository
    ): DownloadsRepo

    @Binds
    fun bindCryptoRepository(
        repo: CryptoRepository
    ): CryptoRepo

    @Binds
    fun bindGitHubAuthRepository(
        repo: GitHubAuthRepository
    ): GitHubAuthRepo

    @Binds
    fun bindGitHubUserRepository(
        repo: GitHubUserRepository
    ): GitHubUserRepo
}