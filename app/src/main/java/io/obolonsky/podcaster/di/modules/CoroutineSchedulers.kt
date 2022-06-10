package io.obolonsky.podcaster.di.modules

import kotlinx.coroutines.CoroutineDispatcher

interface CoroutineSchedulers {

    val main: CoroutineDispatcher

    val io: CoroutineDispatcher

    val computation: CoroutineDispatcher

    val unconfined: CoroutineDispatcher
}