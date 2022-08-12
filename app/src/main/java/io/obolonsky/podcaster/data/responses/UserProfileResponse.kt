package io.obolonsky.podcaster.data.responses

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @SerializedName("userId") val userId: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("ownMaterialsCount") val ownMaterialsCount: Int,
    @SerializedName("balance") val balance: Int,
    @SerializedName("auditionCount") val auditionCount: Int,
    @SerializedName("raiting") val raiting: Int,
)