package io.obolonsky.network.responses.github

import com.google.gson.annotations.SerializedName

data class GitHubUserResponse(
    @SerializedName("id") val id: Long?,
    @SerializedName("login") val login: String?,
    @SerializedName("name") val name: String?,
)