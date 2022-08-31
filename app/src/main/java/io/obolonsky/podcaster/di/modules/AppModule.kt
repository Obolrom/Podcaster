package io.obolonsky.podcaster.di.modules

import dagger.Module

@Module(includes = [
    DatabaseModule::class,
    WebServiceModule::class,
    BinderModule::class,
    WorkerBindingModule::class,
])
class AppModule