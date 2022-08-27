package io.obolonsky.network.di

import io.obolonsky.network.api.SongRecognitionApi

interface SongRecognitionApiProvider {

    val songRecognitionApi: SongRecognitionApi
}