package io.obolonsky.core.di.repositories

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction

interface SpaceXRepo {

    // TODO: fix return type
    suspend fun getNextLaunch(): Reaction<Boolean, Error>
}