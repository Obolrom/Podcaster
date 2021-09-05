package io.obolonsky.podcaster.di.modules

import dagger.Module

@Module(includes = [
    WebServiceModule::class,
    PlayerModule::class,
    DatabaseModule::class
])
class CoreModule