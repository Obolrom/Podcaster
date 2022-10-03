package io.obolonsky.network.apihelpers.base

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction

interface ApiHelper<R, P> {

    suspend fun load(param: P): Reaction<R, Error>
}