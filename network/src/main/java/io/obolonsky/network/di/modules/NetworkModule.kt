package io.obolonsky.network.di.modules

import dagger.Module

@Module(
    includes = [
        RemoteApiModule::class,
        BinderModule::class,
    ]
)
class NetworkModule