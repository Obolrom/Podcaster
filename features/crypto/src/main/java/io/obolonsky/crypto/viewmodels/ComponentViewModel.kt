package io.obolonsky.crypto.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.crypto.di.CryptoComponent

internal class ComponentViewModel(
    application: Application
) : AndroidViewModel(application) {

    val cryptoComponent by lazy {
        val appProvider = (application as App).getAppComponent()

        CryptoComponent.create(
            toolsProvider = appProvider,
            cryptoRepoProvider = appProvider,
        )
    }
}