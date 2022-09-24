package io.obolonsky.downloads.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.downloads.di.DownloadsComponent

internal class ComponentViewModel(app: Application) : AndroidViewModel(app) {

    val downloadsComponent by lazy {
        val applicationProvider = (app as App).getAppComponent()
        DownloadsComponent.create(
            toolsProvider = applicationProvider,
            playerActionProvider = applicationProvider,
            nasaActionsProvider = applicationProvider,
            networkStatusObservableProvider = applicationProvider,
            downloadsActionProvider = applicationProvider,
            downloadsRepoProvider = applicationProvider,
            downloadsStorageProvider = applicationProvider,
        )
    }
}