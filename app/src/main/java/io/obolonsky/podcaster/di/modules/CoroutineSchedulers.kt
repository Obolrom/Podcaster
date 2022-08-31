package io.obolonsky.podcaster.di.modules

import io.obolonsky.core.di.scopes.ApplicationScope
import io.obolonsky.core.di.utils.CoroutineSchedulers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@ApplicationScope
class CoroutinesSchedulersImpl @Inject constructor() : CoroutineSchedulers {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val computation: CoroutineDispatcher = Dispatchers.Default
    override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
}