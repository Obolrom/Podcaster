package io.obolonsky.downloads

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import io.obolonsky.core.di.scopes.FeatureScope
import javax.inject.Inject

@FeatureScope
class DownloadUtils @Inject constructor() {

    companion object {
        const val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "download_channel"

        private const val USE_DECODER_EXTENSIONS = false

        const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"
    }

    private var downloadNotificationHelper: DownloadNotificationHelper? = null

    /** Returns whether extension renderers should be used.  */
    private fun useExtensionRenderers(): Boolean {
        return USE_DECODER_EXTENSIONS
    }

    @OptIn(markerClass = [UnstableApi::class])
    fun buildRenderersFactory(
        context: Context, preferExtensionRenderer: Boolean
    ): RenderersFactory {
        val extensionRendererMode =
            if (useExtensionRenderers()) {
                if (preferExtensionRenderer) DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                else DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON
            } else DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
        return DefaultRenderersFactory(context.applicationContext)
            .setExtensionRendererMode(extensionRendererMode)
    }

    @OptIn(markerClass = [UnstableApi::class])
    @Synchronized
    fun getDownloadNotificationHelper(
        context: Context
    ): DownloadNotificationHelper {
        return downloadNotificationHelper ?: run {
            DownloadNotificationHelper(context, DOWNLOAD_NOTIFICATION_CHANNEL_ID).also {
                downloadNotificationHelper = it
            }
        }
    }
}