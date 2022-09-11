package io.obolonsky.nasa.di.components

import dagger.Component
import io.obolonsky.core.di.depsproviders.ApplicationProvider
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.nasa.ui.NasaActivity

@FeatureScope
@Component(
    dependencies = [
        ApplicationProvider::class,
    ]
)
internal interface NasaComponent {

    @Component.Factory
    interface Factory {

        fun create(
            applicationProvider: ApplicationProvider,
        ): NasaComponent
    }

    fun inject(target: NasaActivity)

    companion object {

        fun create(applicationProvider: ApplicationProvider): NasaComponent {
            return DaggerNasaComponent.factory()
                .create(
                    applicationProvider = applicationProvider,
                )
        }
    }
}