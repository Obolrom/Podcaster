package io.obolonsky.podcaster.di.modules

import dagger.Binds
import dagger.Module
import io.obolonsky.core.di.utils.CoroutineSchedulers

@Module
@Suppress("unused")
interface BinderModule {

    @Binds
    fun provideDispatchers(dispatchers: CoroutinesSchedulersImpl): CoroutineSchedulers
}