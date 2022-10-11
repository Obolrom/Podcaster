package io.obolonsky.crypto.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.crypto.di.CryptoComponent

internal class ComponentViewModel(
    application: Application
) : AndroidViewModel(application) {

    val cryptoComponent by lazy {
        CryptoComponent.create((application as App).getAppComponent())
    }
}