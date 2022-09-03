package io.obolonsky.core.di.depsproviders

import io.obolonsky.core.di.actions.GoToSpaceXAction

interface SpaceXActionsProvider {

    val goToSpaceXAction: GoToSpaceXAction
}