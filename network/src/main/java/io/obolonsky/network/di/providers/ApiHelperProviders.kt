package io.obolonsky.network.di.providers

import io.obolonsky.network.apihelpers.*
import io.obolonsky.network.apihelpers.github.GetGithubSearchReposApiHelper
import io.obolonsky.network.apihelpers.github.GetGithubUserApiHelper
import io.obolonsky.network.apihelpers.github.GetGithubUserProfileApiHelper
import io.obolonsky.network.apihelpers.github.GetGithubViewerProfileApiHelper

interface ApiHelperProviders : SpaceXHelpersProvider {

    val shazamSongRecognitionApiHelper: ShazamSongRecognitionApiHelper

    val getRelatedTracksApiHelper: GetRelatedTracksApiHelper

    val getApodApiHelper: GetApodApiHelper

    val getMarsPhotosApiHelper: GetMarsPhotosApiHelper

    val featureToggleApiHelper: FeatureToggleApiHelper

    val getExchangeRatesHelper: GetExchangeRatesHelper

    val getMonoAccountInfoApiHelper: GetMonoAccountInfoApiHelper

    val getCoinFeedApiHelper: GetCoinFeedApiHelper

    val getCoinDetailsApiHelper: GetCoinDetailsApiHelper

    val getGithubUserApiHelper: GetGithubUserApiHelper

    val getGithubSearchReposApiHelper: GetGithubSearchReposApiHelper

    val getGithubUserProfileApiHelper: GetGithubUserProfileApiHelper

    val getGithubViewerProfileApiHelper: GetGithubViewerProfileApiHelper
}