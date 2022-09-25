package io.obolonsky.media_downloader.di

import dagger.Binds
import dagger.Component
import dagger.Module
import io.obolonsky.core.di.actions.GetDownloadServiceClassAction
import io.obolonsky.core.di.actions.StartDownloadServiceAction
import io.obolonsky.core.di.downloads.providers.GetDownloadServiceClassActionProvider
import io.obolonsky.core.di.downloads.providers.StartDownloadServiceActionProvider
import io.obolonsky.media_downloader.actions.GetDownloadServiceClassActionImpl
import io.obolonsky.media_downloader.actions.StartDownloadServiceActionImpl

@Component(
    modules = [
        MediaDownloadsExportModule::class,
    ]
)
interface MediaDownloadsExportComponent :
    StartDownloadServiceActionProvider,
    GetDownloadServiceClassActionProvider {

    @Component.Factory
    interface Factory {

        fun create(): MediaDownloadsExportComponent
    }

    companion object {

        fun create(): MediaDownloadsExportComponent {
            return DaggerMediaDownloadsExportComponent.create()
        }
    }
}

@Module
@Suppress("unused")
internal interface MediaDownloadsExportModule {

    @Binds
    fun bindGetDownloadServiceClassAction(
        action: GetDownloadServiceClassActionImpl
    ): GetDownloadServiceClassAction

    @Binds
    fun bindStartDownloadServiceActionImpl(
        action: StartDownloadServiceActionImpl
    ): StartDownloadServiceAction
}