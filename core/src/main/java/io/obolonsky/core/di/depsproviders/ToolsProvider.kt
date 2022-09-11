package io.obolonsky.core.di.depsproviders

interface ToolsProvider :
    ApplicationContextProvider,
    CoroutineSchedulersProvider,
    JsonConverterProvider