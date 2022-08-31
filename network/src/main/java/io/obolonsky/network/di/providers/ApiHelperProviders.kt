package io.obolonsky.network.di.providers

import io.obolonsky.network.apihelpers.FeatureToggleApiHelper
import io.obolonsky.network.apihelpers.GetRelatedTracksApiHelper
import io.obolonsky.network.apihelpers.ShazamSongRecognitionApiHelper

interface ApiHelperProviders {

    val shazamSongRecognitionApiHelper: ShazamSongRecognitionApiHelper

    val getRelatedTracksApiHelper: GetRelatedTracksApiHelper

    val featureToggleApiHelper: FeatureToggleApiHelper
}