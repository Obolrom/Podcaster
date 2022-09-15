package io.obolonsky.podcaster.di.modules

import dagger.Binds
import dagger.Module
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.core.di.utils.JsonConverter
import io.obolonsky.podcaster.utils.CoroutinesSchedulersImpl
import io.obolonsky.podcaster.utils.JsonConverterImpl

@Module
@Suppress("unused")
interface ToolsModule {

    @Binds
    fun bindCoroutineSchedulers(
        coroutineSchedulers: CoroutinesSchedulersImpl
    ): CoroutineSchedulers

    @Binds
    fun bindJsonConverter(
        jsonConverter: JsonConverterImpl
    ): JsonConverter
}