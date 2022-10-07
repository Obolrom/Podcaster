package io.obolonsky.spacex.usecases

import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.spaceX.rocket.Rocket
import io.obolonsky.spacex.di.ScopedSpaceXRepo
import javax.inject.Inject

internal class GetRocketFullDetailsUseCase @Inject constructor(
    private val spaceXRepo: ScopedSpaceXRepo,
) {

    suspend operator fun invoke(id: String): Reaction<Rocket?> {
        return spaceXRepo.getRocket(id)
    }
}