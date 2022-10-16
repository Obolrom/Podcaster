package io.obolonsky.crypto.di

import dagger.Binds
import dagger.Component
import dagger.Module
import io.obolonsky.core.di.actions.GoToCryptoAction
import io.obolonsky.core.di.depsproviders.CryptoActionsProvider
import io.obolonsky.crypto.actions.GoToCryptoActionImpl

@Component(
    modules = [
        CryptoExportModule::class,
    ]
)
interface CryptoExportComponent : CryptoActionsProvider {

    @Component.Factory
    interface Factory {

        fun create(): CryptoExportComponent
    }

    companion object {

        fun createCryptoActionsProvider(): CryptoActionsProvider {
            return DaggerCryptoExportComponent.factory()
                .create()
        }
    }
}

@Module
@Suppress("unused")
interface CryptoExportModule {

    @Binds
    fun bindGoToCryptoAction(
        action: GoToCryptoActionImpl
    ): GoToCryptoAction
}