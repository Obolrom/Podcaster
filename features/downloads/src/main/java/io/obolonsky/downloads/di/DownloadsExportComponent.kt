package io.obolonsky.downloads.di

import dagger.Binds
import dagger.Component
import dagger.Module
import io.obolonsky.core.di.actions.NavigateToDownloadsAction
import io.obolonsky.core.di.depsproviders.DownloadsActionProvider
import io.obolonsky.downloads.actions.NavigateToDownloadsActionImpl

@Component(
    modules = [DownloadsExportModule::class]
)
interface DownloadsExportComponent : DownloadsActionProvider {

    @Component.Factory
    interface Factory {

        fun create(): DownloadsExportComponent
    }

    companion object {

        fun create(): DownloadsExportComponent {
            return DaggerDownloadsExportComponent.create()
        }
    }
}

@Module
@Suppress
internal interface DownloadsExportModule {

    @Binds
    fun bindNavigateToDownloadsAction(
        action: NavigateToDownloadsActionImpl
    ): NavigateToDownloadsAction
}