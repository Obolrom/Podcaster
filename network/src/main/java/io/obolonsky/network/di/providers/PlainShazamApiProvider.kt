package io.obolonsky.network.di.providers

import io.obolonsky.network.api.PlainShazamApi

interface PlainShazamApiProvider {

    val plainShazamApi: PlainShazamApi
}