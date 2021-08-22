package io.obolonsky.podcaster.di.injectors

import io.obolonsky.podcaster.PodcasterApp
import io.obolonsky.podcaster.di.AppComponent
import io.obolonsky.podcaster.di.DaggerAppComponent
import io.obolonsky.podcaster.di.modules.AppModule
import io.obolonsky.podcaster.di.modules.CoreModule

object AppInjector {

    lateinit var appComponent: AppComponent

    fun init(app: PodcasterApp) {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(app))
            .coreModule(CoreModule())
            .build()

        appComponent.inject(app)
    }
}