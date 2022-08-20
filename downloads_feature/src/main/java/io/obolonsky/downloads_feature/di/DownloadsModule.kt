package io.obolonsky.downloads_feature.di

import dagger.Module

@Module(
    includes = [
        PlayerModule::class
    ]
)
class DownloadsModule