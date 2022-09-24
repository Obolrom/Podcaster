package io.obolonsky.core.di.downloads.providers

import io.obolonsky.core.di.actions.StartDownloadServiceAction

interface StartDownloadServiceActionProvider {

    val startDownloadServiceAction: StartDownloadServiceAction
}