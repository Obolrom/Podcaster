package io.obolonsky.network.di

import dagger.Module

@Module(
    includes = [
        RemoteApiModule::class,
    ]
)
class NetworkModule