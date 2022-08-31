package io.obolonsky.network.di.providers

import io.obolonsky.network.api.SongRecognitionApi

interface SongRecognitionApiProvider {

    val songRecognitionApi: SongRecognitionApi
}