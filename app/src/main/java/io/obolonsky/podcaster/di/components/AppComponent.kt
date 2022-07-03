package io.obolonsky.podcaster.di.components

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import io.obolonsky.podcaster.PodcasterApp
import io.obolonsky.podcaster.di.modules.AppModule
import io.obolonsky.podcaster.di.scopes.ApplicationScope
import io.obolonsky.podcaster.player.PlayerService
import io.obolonsky.podcaster.ui.MainActivity
import io.obolonsky.podcaster.viewmodels.SongsViewModel

@ApplicationScope
@Component(modules = [AppModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {

        fun context(@BindsInstance appCtx: Context): Builder

        fun build(): AppComponent
    }

    fun inject(target: PodcasterApp)

    fun inject(target: PlayerService)

    fun inject(target: MainActivity)

    fun songsViewModel(): SongsViewModel.Factory
}