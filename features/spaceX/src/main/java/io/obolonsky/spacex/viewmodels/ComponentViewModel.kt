package io.obolonsky.spacex.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.spacex.di.SpaceXComponent

internal class ComponentViewModel(
    application: Application,
) : AndroidViewModel(application) {

    internal val component: SpaceXComponent by lazy {
        SpaceXComponent.create(
            applicationProvider = (application as App).getAppComponent()
        )
    }
}