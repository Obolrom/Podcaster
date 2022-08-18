package io.obolonsky.shazam_feature.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.shazam_feature.di.DaggerShazamComponent
import io.obolonsky.shazam_feature.di.ShazamComponent

internal class ComponentViewModel(
    application: Application,
) : AndroidViewModel(application) {

    val shazamComponent: ShazamComponent by lazy {
        DaggerShazamComponent.factory()
            .create((application as App).getAppComponent())
    }
}