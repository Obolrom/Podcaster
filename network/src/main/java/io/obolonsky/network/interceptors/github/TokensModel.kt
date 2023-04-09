package io.obolonsky.network.interceptors.github

data class TokensModel(
    val accessToken: String,
    val refreshToken: String,
    val idToken: String
)