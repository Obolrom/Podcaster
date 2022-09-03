package io.obolonsky.core.di.data.spaceX.rocket

data class Rocket(
    val id: String?,
    val name: String?,
    val country: String?,
    val costPerLaunch: Int?,
    val company: String?,
    val boosters: Int?,
    val active: Boolean?,
    val stages: Int?,
    val successRatePct: Int?,
    val type: String?,
    val wikipedia: String?,
    val secondStage: SecondStage?,
    val payloadWeights: List<PayloadWeight?>?,
    val description: String?,
    val engines: Engines?,
    val firstFlight: Any?,
)