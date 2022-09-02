package io.obolonsky.downloads.di

import dagger.Component
import dagger.Module
import dagger.Provides
import io.obolonsky.core.di.actions.NavigateToDownloadsAction
import io.obolonsky.core.di.depsproviders.DownloadsActionProvider

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
class DownloadsExportModule {

    @Provides
    fun provideNavigateToDownloadsAction(): NavigateToDownloadsAction {
        error("Download navigation dependency is not implemented. Probably you use fake module")
    }
}