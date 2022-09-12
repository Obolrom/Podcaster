package io.obolonsky.network.di.modules

import dagger.Binds
import dagger.Module
import io.obolonsky.network.utils.RxSchedulers
import io.obolonsky.network.utils.RxSchedulersImpl

@Module
@Suppress
interface BinderModule {

    @Binds
    fun bindRxSchedulers(
        rxSchedulers: RxSchedulersImpl
    ): RxSchedulers
}