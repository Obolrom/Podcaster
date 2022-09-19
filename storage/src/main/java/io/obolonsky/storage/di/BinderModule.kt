package io.obolonsky.storage.di

import dagger.Binds
import dagger.Module
import io.obolonsky.core.di.downloads.DownloadsStorage
import io.obolonsky.storage.downloads.DownloadsStorageImpl

@Module
internal interface BinderModule {

    @Binds
    fun bindDownloadsStorage(
        storage: DownloadsStorageImpl
    ): DownloadsStorage
}