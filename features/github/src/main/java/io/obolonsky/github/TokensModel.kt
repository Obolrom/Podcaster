package io.obolonsky.github

data class TokensModel(
    val accessToken: String,
    val refreshToken: String,
    val idToken: String
)