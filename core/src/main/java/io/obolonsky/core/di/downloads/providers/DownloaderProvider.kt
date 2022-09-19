package io.obolonsky.core.di.downloads.providers

import io.obolonsky.core.di.downloads.Downloader

interface DownloaderProvider {

    val downloader: Downloader
}