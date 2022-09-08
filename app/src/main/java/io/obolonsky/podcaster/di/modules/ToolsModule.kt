package io.obolonsky.podcaster.di.modules

import dagger.Binds
import dagger.Module
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.podcaster.utils.CoroutinesSchedulersImpl

@Module
@Suppress("unused")
interface ToolsModule {

    @Binds
    fun bindCoroutineSchedulers(
        coroutineSchedulers: CoroutinesSchedulersImpl
    ): CoroutineSchedulers
}