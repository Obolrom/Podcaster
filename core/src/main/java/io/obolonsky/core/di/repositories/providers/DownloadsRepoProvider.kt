package io.obolonsky.core.di.repositories.providers

import io.obolonsky.core.di.repositories.DownloadsRepo

interface DownloadsRepoProvider {

    val downloadsRepo: DownloadsRepo
}