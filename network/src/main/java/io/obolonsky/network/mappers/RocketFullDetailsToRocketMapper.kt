package io.obolonsky.network.mappers

import io.obolonsky.core.di.data.spaceX.rocket.Engines
import io.obolonsky.core.di.data.spaceX.rocket.PayloadWeight
import io.obolonsky.core.di.data.spaceX.rocket.Rocket
import io.obolonsky.core.di.data.spaceX.rocket.SecondStage
import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.RocketFullDetailsQuery

object RocketFullDetailsToRocketMapper : Mapper<RocketFullDetailsQuery.Data, Rocket?> {

    override fun map(input: RocketFullDetailsQuery.Data): Rocket? {
        val rocket = input.rocket ?: return null

        val secondStage = SecondStage(
            engines = rocket.second_stage?.engines,
            fuelAmountTons = rocket.second_stage?.fuel_amount_tons,
        )

        return Rocket(
            id = rocket.id,
            name = rocket.name,
            country = rocket.country,
            costPerLaunch = rocket.cost_per_launch,
            company = rocket.company,
            boosters = rocket.boosters,
            active = rocket.active,
            stages = rocket.stages,
            successRatePct = rocket.success_rate_pct,
            type = rocket.type,
            wikipedia = rocket.wikipedia,
            secondStage = secondStage,
            payloadWeights = rocket.payload_weights?.mapPayloadWeights(),
            description = rocket.description,
            engines = rocket.engines?.mapEngines(),
            firstFlight = rocket.first_flight,
        )
    }

    private fun List<RocketFullDetailsQuery.Payload_weight?>.mapPayloadWeights():
            List<PayloadWeight> = map {
        PayloadWeight(
            id = it?.id,
            kg = it?.kg,
            name = it?.name,
        )
    }

    private fun RocketFullDetailsQuery.Engines?.mapEngines(): Engines {
        return Engines(
            layout = this?.layout,
            engineLossMax = this?.engine_loss_max,
            number = this?.number,
            propellant1 = this?.propellant_1,
            propellant2 = this?.propellant_2,
            thrustToWeight = this?.thrust_to_weight,
            type = this?.type,
            version = this?.version,
        )
    }
}