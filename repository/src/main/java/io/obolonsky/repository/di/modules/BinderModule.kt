package io.obolonsky.repository.di.modules

import dagger.Binds
import dagger.Module
import io.obolonsky.core.di.repositories.FeatureTogglesRepo
import io.obolonsky.core.di.repositories.NasaRepo
import io.obolonsky.core.di.repositories.ShazamRepo
import io.obolonsky.core.di.repositories.SpaceXRepo
import io.obolonsky.repository.FeatureTogglesRepository
import io.obolonsky.repository.NasaRepository
import io.obolonsky.repository.ShazamRepository
import io.obolonsky.repository.SpaceXRepository

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
}