package io.obolonsky.network.di.providers

import io.obolonsky.network.apihelpers.*
import io.obolonsky.network.apihelpers.github.*

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

    val getGithubRepoApiHelper: GetGithubRepoApiHelper

    val getLastCommitForEntryApiHelper: GetLastCommitForEntryApiHelper

    val addStarForRepoApiHelper: AddStarForRepoApiHelper

    val removeStarForRepoApiHelper: RemoveStarForRepoApiHelper

    val getGithubRepoBranches: GetGithubRepoBranches

    val getGithubViewerReposApiHelper: GetGithubViewerReposApiHelper
}