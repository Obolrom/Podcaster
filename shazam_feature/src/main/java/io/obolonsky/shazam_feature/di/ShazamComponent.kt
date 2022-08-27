package io.obolonsky.shazam_feature.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.ApplicationProvider
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.shazam_feature.ui.ShazamActivity

@FeatureScope
@Component(
    dependencies = [ApplicationProvider::class],
    modules = [ShazamModule::class]
)
internal interface ShazamComponent : AssistedFactoriesModule {

    @Component.Factory
    interface Factory {

        fun create(
            appCtxProvider: ApplicationProvider,
        ): ShazamComponent
    }

    fun inject(target: ShazamActivity)
}