package io.obolonsky.core.di.depsproviders

import io.obolonsky.core.di.actions.CreatePlayerScreenAction
import io.obolonsky.core.di.actions.StopPlayerService

interface PlayerActionProvider {

    fun providePlayerAction(): CreatePlayerScreenAction

    fun provideStopPlayerServiceAction(): StopPlayerService
}