package io.obolonsky.podcaster.di.modules

import dagger.Binds
import dagger.Module

@Module
@Suppress("unused")
interface BinderModule {

    @Binds
    fun provideDispatchers(dispatchers: CoroutinesSchedulersImpl): CoroutineSchedulers
}