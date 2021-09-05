package io.obolonsky.podcaster.di.modules

import dagger.Module

@Module(includes = [
    WebServiceModule::class,
    PlayerModule::class,
])
class CoreModule