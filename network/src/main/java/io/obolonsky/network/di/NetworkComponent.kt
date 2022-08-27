package io.obolonsky.network.di

import dagger.Component

@Component(
    dependencies = [],
    modules = [
        NetworkModule::class,
    ]
)
interface NetworkComponent : NetworkClientsProvider {

    @Component.Factory
    interface Factory {

        fun create(): NetworkComponent
    }
}