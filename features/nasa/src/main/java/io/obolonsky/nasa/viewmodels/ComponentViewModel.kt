package io.obolonsky.nasa.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.nasa.di.components.NasaComponent

internal class ComponentViewModel(
    application: Application,
) : AndroidViewModel(application) {

    internal val component: NasaComponent by lazy {
        val appProvider = (application as App).getAppComponent()

        NasaComponent.create(
            toolsProvider = appProvider,
            nasaRepoProvider = appProvider,
        )
    }
}