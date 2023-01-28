package io.obolonsky.github.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.github.di.GitHubComponent

internal class ComponentViewModel(app: Application) : AndroidViewModel(app) {

    val gitHubComponent by lazy {
        val applicationProvider = (app as App).getAppComponent()

        GitHubComponent.create(
            toolsProvider = applicationProvider,
        )
    }
}