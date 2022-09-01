package io.obolonsky.core.di.depsproviders

import io.obolonsky.core.di.repositories.providers.RepositoryProvider

interface ApplicationProvider :
    ToolsProvider,
    PlayerActionProvider,
    NetworkStatusObservableProvider,
    NavigateToExoPlayerActionProvider,
    RepositoryProvider