package io.obolonsky.core.di.downloads.providers

import io.obolonsky.core.di.actions.GetDownloadServiceClassAction

interface GetDownloadServiceClassActionProvider {

    val getDownloadServiceClassAction: GetDownloadServiceClassAction
}