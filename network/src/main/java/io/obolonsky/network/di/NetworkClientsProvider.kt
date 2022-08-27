package io.obolonsky.network.di

import io.obolonsky.network.api.SongRecognitionApi

interface NetworkClientsProvider {

    val songRecognitionApi: SongRecognitionApi
}