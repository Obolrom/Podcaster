package io.obolonsky.network.responses.featuretoggles

import com.google.gson.annotations.SerializedName

data class FeatureTogglesResponse(
    @SerializedName("shazam") val shazam: ShazamToggleResponse?,
) {

    data class ShazamToggleResponse(
        @SerializedName("is_working") val isWorking: Boolean?,
    )
}
