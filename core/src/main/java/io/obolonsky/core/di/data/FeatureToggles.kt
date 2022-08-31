package io.obolonsky.core.di.data

data class FeatureToggles(
    val shazam: Shazam,
) {

    data class Shazam(
        val isWorking: Boolean,
    )
}