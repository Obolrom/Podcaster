package io.obolonsky.network.di.providers

interface NetworkClientsProvider :
    SongRecognitionApiProvider,
    PlainShazamApiProvider,
    FeatureTogglesApiProvider