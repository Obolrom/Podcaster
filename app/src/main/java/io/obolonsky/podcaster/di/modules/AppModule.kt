package io.obolonsky.podcaster.di.modules

import dagger.Module
import dagger.Provides
import io.obolonsky.podcaster.PodcasterApp

@Module
class AppModule(private val application: PodcasterApp) {

    @Provides
    fun getContext() = application.applicationContext
}