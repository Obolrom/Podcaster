package io.obolonsky.network.apihelpers

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction

interface ApiHelper<D, E : Error> {

    suspend fun load(): Reaction<D, E>
}