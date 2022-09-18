package io.obolonsky.core.di.repositories.providers

interface RepositoryProvider :
    ShazamRepoProvider,
    FeatureTogglesRepoProvider,
    SpaceXRepoProvider,
    NasaRepoProvider,
    BanksRepoProvider,
    DownloadsRepoProvider