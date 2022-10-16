package io.obolonsky.core.di.depsproviders

import io.obolonsky.core.di.actions.GoToCryptoAction

interface CryptoActionsProvider {

    val goToCryptoAction: GoToCryptoAction
}