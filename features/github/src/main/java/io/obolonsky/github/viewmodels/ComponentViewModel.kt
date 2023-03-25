package io.obolonsky.github.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.github.di.DaggerGitHubComponent
import io.obolonsky.github.di.GitHubComponent

internal class ComponentViewModel(app: Application) : AndroidViewModel(app) {

    val gitHubComponent: GitHubComponent by lazy {
        val applicationProvider = (app as App).getAppComponent()

        DaggerGitHubComponent.factory()
            .create(
                applicationProvider,
                applicationProvider,
                applicationProvider,
                applicationProvider,
                applicationProvider,
            )
    }
}