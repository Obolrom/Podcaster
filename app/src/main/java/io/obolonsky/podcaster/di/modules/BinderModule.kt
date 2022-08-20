package io.obolonsky.podcaster.di.modules

import dagger.Binds
import dagger.Module
import io.obolonsky.core.di.actions.NavigateToExoPlayerAction
import io.obolonsky.core.di.actions.ShowPlayer
import io.obolonsky.core.di.actions.StopPlayerService
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.podcaster.actions.GoToPlayerAction
import io.obolonsky.podcaster.actions.StopPlayerServiceAction
import io.obolonsky.podcaster.misc.NavigateToExoPlayerActionImpl

@Module
@Suppress("unused")
interface BinderModule {

    @Binds
    fun bindDispatchers(dispatchers: CoroutinesSchedulersImpl): CoroutineSchedulers

    @Binds
    fun bindGoToPlayerAction(action: GoToPlayerAction): ShowPlayer

    @Binds
    fun bindStopPlayerService(action: StopPlayerServiceAction): StopPlayerService

    @Binds
    fun bindNavigateToExoPlayerAction(
        action: NavigateToExoPlayerActionImpl
    ): NavigateToExoPlayerAction
}