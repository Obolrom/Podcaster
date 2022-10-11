package io.obolonsky.crypto.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.ApplicationProvider
import io.obolonsky.core.di.scopes.FeatureScope

@FeatureScope
@Component(
    dependencies = [
        ApplicationProvider::class,
    ]
)
internal interface CryptoComponent : ViewModelProviders {

    @Component.Factory
    interface Factory {

        fun create(
            applicationProvider: ApplicationProvider,
        ): CryptoComponent
    }

    companion object {

        fun create(applicationProvider: ApplicationProvider): CryptoComponent {
            return DaggerCryptoComponent.factory()
                .create(
                    applicationProvider = applicationProvider,
                )
        }
    }
}