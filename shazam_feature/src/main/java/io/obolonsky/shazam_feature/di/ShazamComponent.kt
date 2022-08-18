package io.obolonsky.shazam_feature.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.ApplicationProvider
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.shazam_feature.ui.ShazamActivity
import io.obolonsky.shazam_feature.ui.ShazamViewModel

@FeatureScope
@Component(
    dependencies = [ApplicationProvider::class],
    modules = [ShazamModule::class]
)
interface ShazamComponent {

    @Component.Factory
    interface Factory {

        fun create(
            appCtxProvider: ApplicationProvider,
        ): ShazamComponent
    }

    fun inject(target: ShazamActivity)

    fun shazamViewModel(): ShazamViewModel.Factory
}