package io.obolonsky.podcaster.di.modules

import dagger.Binds
import dagger.Module
import io.obolonsky.core.di.utils.CoroutineSchedulers

@Module
@Suppress("unused")
interface ToolsModule {

    @Binds
    fun bindCoroutineSchedulers(
        coroutineSchedulers: CoroutinesSchedulersImpl
    ): CoroutineSchedulers
}