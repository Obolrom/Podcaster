package io.obolonsky.core.di.depsproviders

interface ApplicationProvider : ApplicationContextProvider,
    CoroutineSchedulersProvider,
    ActionProvider,
    NavigateToExoPlayerActionProvider