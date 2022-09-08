package io.obolonsky.podcaster.di.modules

import dagger.Module

@Module(
    includes = [
        BinderModule::class,
        WorkerBindingModule::class,
    ]
)
class AppModule