package io.obolonsky.network.mappers

import io.obolonsky.core.di.data.spaceX.rocket.Engines
import io.obolonsky.core.di.data.spaceX.rocket.PayloadWeight
import io.obolonsky.core.di.data.spaceX.rocket.Rocket
import io.obolonsky.core.di.data.spaceX.rocket.SecondStage
import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.RocketFullDetailsQuery

object RocketFullDetailsToRocketMapper : Mapper<RocketFullDetailsQuery.Rocket, Rocket?> {

    override fun map(input: RocketFullDetailsQuery.Rocket): Rocket? {
        val secondStage = SecondStage(
            engines = input.second_stage?.engines,
            fuelAmountTons = input.second_stage?.fuel_amount_tons,
        )

        return Rocket(
            id = input.id,
            name = input.name,
            country = input.country,
            costPerLaunch = input.cost_per_launch,
            company = input.company,
            boosters = input.boosters,
            active = input.active,
            stages = input.stages,
            successRatePct = input.success_rate_pct,
            type = input.type,
            wikipedia = input.wikipedia,
            secondStage = secondStage,
            payloadWeights = input.payload_weights?.mapPayloadWeights(),
            description = input.description,
            engines = input.engines?.mapEngines(),
            firstFlight = input.first_flight,
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