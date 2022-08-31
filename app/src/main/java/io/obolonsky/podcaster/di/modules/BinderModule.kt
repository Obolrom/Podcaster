package io.obolonsky.podcaster.di.modules

import dagger.Binds
import dagger.Module
import io.obolonsky.core.di.actions.NavigateToExoPlayerAction
import io.obolonsky.podcaster.misc.NavigateToExoPlayerActionImpl

@Module
@Suppress("unused")
interface BinderModule {

    @Binds
    fun bindNavigateToExoPlayerAction(
        action: NavigateToExoPlayerActionImpl
    ): NavigateToExoPlayerAction
}