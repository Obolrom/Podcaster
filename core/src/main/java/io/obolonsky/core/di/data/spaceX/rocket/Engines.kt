package io.obolonsky.core.di.data.spaceX.rocket

data class Engines(
    val layout: String?,
    val engineLossMax: String?,
    val number: Int?,
    val propellant1: String?,
    val propellant2: String?,
    val thrustToWeight: Double?,
    val type: String?,
    val version: String?,
)