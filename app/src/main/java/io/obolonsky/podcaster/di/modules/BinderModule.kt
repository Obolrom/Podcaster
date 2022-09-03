package io.obolonsky.podcaster.di.modules

import dagger.Binds
import dagger.Module
import io.obolonsky.core.di.utils.NetworkStatusObservable
import io.obolonsky.podcaster.utils.NetworkStatusObservableImpl

@Module
@Suppress("unused")
interface BinderModule {

    @Binds
    fun bindNetworkStatusObservable(
        observable: NetworkStatusObservableImpl
    ): NetworkStatusObservable
}