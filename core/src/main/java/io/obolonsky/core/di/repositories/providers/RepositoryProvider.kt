package io.obolonsky.core.di.repositories.providers

import io.obolonsky.core.di.downloads.providers.DownloadsStorageProvider

interface RepositoryProvider :
    ShazamRepoProvider,
    FeatureTogglesRepoProvider,
    SpaceXRepoProvider,
    NasaRepoProvider,
    BanksRepoProvider,
    DownloadsRepoProvider,
    DownloadsStorageProvider,
    CryptoRepoProvider,
    GitHubAuthRepoProvider,
    GitHubUserRepoProvider