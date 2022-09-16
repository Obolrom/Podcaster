package io.obolonsky.network.di.providers

import io.obolonsky.network.apihelpers.*

interface ApiHelperProviders : SpaceXHelpersProvider {

    val shazamSongRecognitionApiHelper: ShazamSongRecognitionApiHelper

    val getRelatedTracksApiHelper: GetRelatedTracksApiHelper

    val getApodApiHelper: GetApodApiHelper

    val getMarsPhotosApiHelper: GetMarsPhotosApiHelper

    val featureToggleApiHelper: FeatureToggleApiHelper

    val getExchangeRatesHelper: GetExchangeRatesHelper
}