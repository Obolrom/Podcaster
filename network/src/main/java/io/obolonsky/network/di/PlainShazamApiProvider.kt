package io.obolonsky.network.di

import io.obolonsky.network.api.PlainShazamApi

interface PlainShazamApiProvider {

    val plainShazamApi: PlainShazamApi
}