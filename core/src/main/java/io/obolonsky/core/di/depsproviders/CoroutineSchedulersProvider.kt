package io.obolonsky.core.di.depsproviders

import io.obolonsky.core.di.utils.CoroutineSchedulers

interface CoroutineSchedulersProvider {

    val coroutineSchedulers: CoroutineSchedulers
}