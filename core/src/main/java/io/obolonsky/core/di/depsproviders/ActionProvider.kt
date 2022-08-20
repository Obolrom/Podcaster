package io.obolonsky.core.di.depsproviders

import io.obolonsky.core.di.actions.ShowPlayer
import io.obolonsky.core.di.actions.StopPlayerService

interface ActionProvider {

    fun providePlayerAction(): ShowPlayer

    fun provideStopPlayerServiceAction(): StopPlayerService
}