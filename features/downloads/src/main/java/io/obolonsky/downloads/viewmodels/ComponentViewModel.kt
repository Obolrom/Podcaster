package io.obolonsky.downloads.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.downloads.di.DownloadsComponent

internal class ComponentViewModel(
    application: Application
) : AndroidViewModel(application) {

    val downloadComponent by lazy {
        DownloadsComponent.create((application as App).getAppComponent())
    }
}