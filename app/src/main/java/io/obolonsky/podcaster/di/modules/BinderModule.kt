package io.obolonsky.podcaster.di.modules

import dagger.Binds
import dagger.Module
import io.obolonsky.core.di.actions.NavigateToExoPlayerAction
import io.obolonsky.core.di.utils.NetworkStatusObservable
import io.obolonsky.podcaster.misc.NavigateToExoPlayerActionImpl
import io.obolonsky.podcaster.utils.NetworkStatusObservableImpl

@Module
@Suppress("unused")
interface BinderModule {

    @Binds
    fun bindNavigateToExoPlayerAction(
        action: NavigateToExoPlayerActionImpl
    ): NavigateToExoPlayerAction

    @Binds
    fun bindNetworkStatusObservable(
        observable: NetworkStatusObservableImpl
    ): NetworkStatusObservable
}