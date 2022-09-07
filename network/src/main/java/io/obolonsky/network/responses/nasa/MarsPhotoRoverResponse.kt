package io.obolonsky.network.responses.nasa

import com.google.gson.annotations.SerializedName

data class MarsPhotoRoverResponse(
    @SerializedName("photos") val photos: List<MarsPhoto?>?,
) {

    data class MarsPhoto(
        @SerializedName("id") val id: Int?,
        @SerializedName("sol") val sol: Int?,
        @SerializedName("camera") val camera: CameraResponse?,
        @SerializedName("img_src") val imageUrl: String?,
        @SerializedName("earth_date") val earthDate: String?,
        @SerializedName("rover") val rover: RoverResponse?,
    ) {

        data class CameraResponse(
            @SerializedName("id") val id: Int?,
            @SerializedName("name") val name: String?,
            @SerializedName("rover_id") val roverId: Int?,
            @SerializedName("full_name") val fullName: String?,
        )

        data class RoverResponse(
            @SerializedName("id") val id: Int?,
            @SerializedName("name") val name: String?,
            @SerializedName("landing_date") val landingDate: String?,
            @SerializedName("launch_date") val launchDate: String?,
            @SerializedName("status") val status: String?,
        )
    }
}