package io.obolonsky.network.di.modules

import dagger.Module
import io.obolonsky.network.di.modules.RemoteApiModule

@Module(
    includes = [
        RemoteApiModule::class,
    ]
)
class NetworkModule