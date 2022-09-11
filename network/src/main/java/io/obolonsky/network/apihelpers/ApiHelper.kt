package io.obolonsky.network.apihelpers

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction

interface ApiHelper<D, P> {

    suspend fun load(param: P): Reaction<D, Error>
}