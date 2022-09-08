package io.obolonsky.network.apihelpers

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction

interface ApiHelperWith3Params<D, E : Error, P1, P2, P3> {

    suspend fun load(
        param1: P1,
        param2: P2,
        param3: P3,
    ): Reaction<D, E>
}