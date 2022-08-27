package io.obolonsky.repository.di.modules

import dagger.Binds
import dagger.Module
import io.obolonsky.core.di.repositories.ShazamRepo
import io.obolonsky.repository.ShazamRepository

@Module
interface BinderModule {

    @Binds
    fun bindShazamRepository(
        shazamRepository: ShazamRepository
    ): ShazamRepo
}