package io.obolonsky.core.di.repositories

import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.spaceX.rocket.Rocket

interface SpaceXRepo {

    // TODO: fix return type
    suspend fun getNextLaunch(): Reaction<Boolean>

    suspend fun getRocket(id: String): Reaction<Rocket?>
}