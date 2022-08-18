package io.obolonsky.core.di.utils

import kotlinx.coroutines.CoroutineDispatcher

interface CoroutineSchedulers {

    val main: CoroutineDispatcher

    val io: CoroutineDispatcher

    val computation: CoroutineDispatcher

    val unconfined: CoroutineDispatcher
}