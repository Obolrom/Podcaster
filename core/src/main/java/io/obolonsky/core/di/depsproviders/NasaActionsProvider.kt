package io.obolonsky.core.di.depsproviders

import io.obolonsky.core.di.actions.GoToNasaAction

interface NasaActionsProvider {

    val goToNasaAction: GoToNasaAction
}