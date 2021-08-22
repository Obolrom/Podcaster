package io.obolonsky.podcaster.di

import dagger.Component
import dagger.android.AndroidInjector
import io.obolonsky.podcaster.PodcasterApp
import io.obolonsky.podcaster.di.injectors.Injector
import io.obolonsky.podcaster.di.modules.AppModule
import io.obolonsky.podcaster.di.modules.CoreModule
import io.obolonsky.podcaster.di.modules.ViewModelModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    ViewModelModule::class,
    CoreModule::class,
])
interface AppComponent:
        AndroidInjector<PodcasterApp>,
        Injector