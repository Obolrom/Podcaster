package io.obolonsky.repository

import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.spaceX.rocket.Rocket
import io.obolonsky.core.di.repositories.SpaceXRepo
import io.obolonsky.network.apihelpers.GetLaunchNextApiHelper
import io.obolonsky.network.apihelpers.GetRocketDetailsApiHelper
import javax.inject.Inject

class SpaceXRepository @Inject constructor(
    private val launchNextApiHelper: GetLaunchNextApiHelper,
    private val rocketDetailsApiHelper: GetRocketDetailsApiHelper,
) : SpaceXRepo {

    override suspend fun getNextLaunch(): Reaction<Boolean> {
        return launchNextApiHelper.load(Unit)
    }

    override suspend fun getRocket(id: String): Reaction<Rocket?> {
        return rocketDetailsApiHelper.load(id)
    }
}