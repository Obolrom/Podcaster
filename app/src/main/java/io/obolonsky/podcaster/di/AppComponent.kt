package io.obolonsky.podcaster.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import io.obolonsky.podcaster.PodcasterApp
import io.obolonsky.podcaster.di.injectors.Injector
import io.obolonsky.podcaster.di.modules.AppModule
import io.obolonsky.podcaster.di.modules.CoreModule
import io.obolonsky.podcaster.di.modules.ViewModelModule
import javax.inject.Singleton

@Component(modules = [
    AppModule::class,
    ViewModelModule::class,
    CoreModule::class,
])
@Singleton
interface AppComponent: AndroidInjector<PodcasterApp>, Injector {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        @BindsInstance
        fun application(app: PodcasterApp): Builder

        fun build(): AppComponent

    }

}