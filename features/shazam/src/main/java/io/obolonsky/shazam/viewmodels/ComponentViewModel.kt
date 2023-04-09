package io.obolonsky.shazam.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.shazam.di.DaggerShazamComponent
import io.obolonsky.shazam.di.ShazamComponent

internal class ComponentViewModel(
    application: Application,
) : AndroidViewModel(application) {

    val shazamComponent: ShazamComponent by lazy {
        val appProvider = (application as App).getAppComponent()

        DaggerShazamComponent.factory()
            .create(
                toolsProvider = appProvider,
                shazamRepoProvider = appProvider,
                playerActionProvider = appProvider,
                downloadsActionProvider = appProvider,
            )
    }
}