package io.obolonsky.repository

import io.obolonsky.core.di.Error
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

    override suspend fun getNextLaunch(): Reaction<Boolean, Error> {
        return when (launchNextApiHelper.load()) {
            is Reaction.Success -> {
                Reaction.Success(true)
            }
            is Reaction.Fail -> Reaction.Success(false)
        }
    }

    override suspend fun getRocket(id: String): Reaction<Rocket?, Error> {
        return rocketDetailsApiHelper.load(id)
    }
}