package io.obolonsky.core.di.depsproviders

import io.obolonsky.core.di.actions.NavigateToDownloadsAction

interface DownloadsActionProvider {

    val navigateToDownloadsAction: NavigateToDownloadsAction
}