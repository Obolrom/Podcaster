package io.obolonsky.podcaster.di.components

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import io.obolonsky.core.di.depsproviders.ApplicationProvider
import io.obolonsky.podcaster.PodcasterApp
import io.obolonsky.podcaster.di.modules.AppModule
import io.obolonsky.core.di.scopes.ApplicationScope
import io.obolonsky.podcaster.ui.BookFeedFragment
import io.obolonsky.podcaster.ui.MainActivity
import io.obolonsky.podcaster.ui.ShazamActivity
import io.obolonsky.podcaster.viewmodels.ShazamViewModel
import io.obolonsky.podcaster.viewmodels.SongsViewModel

@ApplicationScope
@Component(modules = [AppModule::class])
interface AppComponent : ApplicationProvider {

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance appCtx: Context
        ): AppComponent
    }

    fun inject(target: PodcasterApp)

    fun inject(target: MainActivity)

    fun inject(target: ShazamActivity)

    fun inject(target: BookFeedFragment)

    fun songsViewModel(): SongsViewModel.Factory

    fun shazamViewModel(): ShazamViewModel.Factory
}