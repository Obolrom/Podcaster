package io.obolonsky.repository.di.modules

import dagger.Binds
import dagger.Module
import io.obolonsky.core.di.repositories.*
import io.obolonsky.repository.*

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
}