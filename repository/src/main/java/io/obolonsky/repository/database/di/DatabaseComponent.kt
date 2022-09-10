package io.obolonsky.repository.database.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.ApplicationContextProvider
import io.obolonsky.core.di.depsproviders.CoroutineSchedulersProvider
import io.obolonsky.core.di.scopes.ApplicationScope

@ApplicationScope
@Component(
    dependencies = [
        ApplicationContextProvider::class,
        CoroutineSchedulersProvider::class,
    ],
    modules = [
        DatabaseModule::class,
    ]
)
interface DatabaseComponent : DatabaseComponentProvider {

    @Component.Factory
    interface Factory {

        fun create(
            applicationContextProvider: ApplicationContextProvider,
            coroutineSchedulersProvider: CoroutineSchedulersProvider,
        ): DatabaseComponent
    }

    companion object {

        fun create(
            applicationContextProvider: ApplicationContextProvider,
            coroutineSchedulersProvider: CoroutineSchedulersProvider,
        ) = DaggerDatabaseComponent.factory()
            .create(
                applicationContextProvider = applicationContextProvider,
                coroutineSchedulersProvider = coroutineSchedulersProvider,
            )
    }
}