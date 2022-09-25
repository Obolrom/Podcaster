package io.obolonsky.media_downloader.actions

import io.obolonsky.core.di.actions.GetDownloadServiceClassAction
import io.obolonsky.media_downloader.MediaDownloadService
import javax.inject.Inject

internal class GetDownloadServiceClassActionImpl @Inject constructor() :
    GetDownloadServiceClassAction {

    override val downloadServiceClass = MediaDownloadService::class.java
}