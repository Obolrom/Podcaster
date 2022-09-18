package io.obolonsky.downloads.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.downloads.DownloadTracker
import io.obolonsky.downloads.DownloadUtils
import io.obolonsky.downloads.DownloadUtils.getDownloadTracker
import io.obolonsky.downloads.MediaDownloadService
import io.obolonsky.downloads.R
import io.obolonsky.downloads.databinding.ActivityPlayerBinding
import io.obolonsky.downloads.viewmodels.ComponentViewModel
import timber.log.Timber

class DownloadsActivity : AppCompatActivity(), DownloadTracker.Listener {

    private val componentViewModel by viewModels<ComponentViewModel>()

    private val downloadsViewModel by lazyViewModel { savedStateHandle ->
        componentViewModel.downloadComponent
            .downloadsViewModelFactory()
            .create(savedStateHandle)
    }

    private val binding by viewBinding<ActivityPlayerBinding>()

    private var useExtensionRenderers = DownloadUtils.useExtensionRenderers()
    private val downloadTracker by lazy { getDownloadTracker(this) }

    private val mediaUrl = "https://images-assets.nasa.gov/video/0200306-SpaceX_CRS_20_Live_Launch_Coverage_ISO_String-3243998/0200306-SpaceX_CRS_20_Live_Launch_Coverage_ISO_String-3243998~orig.mp4"

    private lateinit var dataSourceFactory: DataSource.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        componentViewModel.downloadComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        dataSourceFactory = DownloadUtils.getDataSourceFactory(this)

        binding.download.setOnClickListener {
            downloadTracker.addListener(this)

            downloadTracker.toggleDownload(
                mediaItem = MediaItem.fromUri(mediaUrl),
                DownloadUtils.buildRenderersFactory(
                    context = this,
                    preferExtensionRenderer = false,
                )
            )
        }

        startDownloadService()
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT > 23) {
            initializePlayer()
            binding.player.onResume()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT <= 23 || binding.player.player == null) {
            initializePlayer()
            binding.player.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT <= 23) {
            binding.player.onPause()
//            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT > 23) {
            binding.player.onPause()
//            releasePlayer()
        }
    }

    private fun initializePlayer(): Boolean {
        val player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(dataSourceFactory)
            )
            .build()
        binding.player.player = player
        player.seekTo(0, 0)
        player.setMediaItem(
            MediaItem.fromUri(mediaUrl)
        )
        player.prepare()
        return true
    }

    override fun onDownloadsChanged() {
        Timber.d("customDownloads onDownloadsChanged")
    }

    @OptIn(markerClass = [UnstableApi::class])
    private fun startDownloadService() {
        // Starting the service in the foreground causes notification flicker if there is no scheduled
        // action. Starting it in the background throws an exception if the app is in the background too
        // (e.g. if device screen is locked).
        try {
            DownloadService.start(this, MediaDownloadService::class.java)
        } catch (e: IllegalStateException) {
            DownloadService.startForeground(this, MediaDownloadService::class.java)
        }
    }

}