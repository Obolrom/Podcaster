package io.obolonsky.nasa.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.nasa.di.components.NasaComponent

internal class ComponentViewModel(
    application: Application,
) : AndroidViewModel(application) {

    internal val component: NasaComponent by lazy {
        NasaComponent.create(
            applicationProvider = (application as App).getAppComponent()
        )
    }
}