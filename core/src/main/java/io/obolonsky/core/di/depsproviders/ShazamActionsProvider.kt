package io.obolonsky.core.di.depsproviders

import io.obolonsky.core.di.actions.GoToShazamAction

interface ShazamActionsProvider {

    val goToShazamAction: GoToShazamAction
}