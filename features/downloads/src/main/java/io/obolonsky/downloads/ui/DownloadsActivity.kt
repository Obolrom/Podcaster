package io.obolonsky.downloads.ui

import android.os.Build
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.core.di.downloads.Downloader
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.core.di.utils.reactWith
import io.obolonsky.downloads.DownloadUtils
import io.obolonsky.downloads.MediaDownloadService
import io.obolonsky.downloads.R
import io.obolonsky.downloads.TrackAdapter
import io.obolonsky.downloads.databinding.ActivityPlayerBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class DownloadsActivity : AppCompatActivity() {

    @Inject
    internal lateinit var downloadsUtils: DownloadUtils

    @Inject
    internal lateinit var downloadTracker: Downloader

    @Inject
    internal lateinit var dataSourceFactory: CacheDataSource.Factory

    private val downloadsViewModel by lazyViewModel { savedStateHandle ->
        MediaDownloadService.getComponent((applicationContext as App).getAppComponent())
            .downloadsViewModelFactory()
            .create(savedStateHandle)
    }

    private val binding by viewBinding<ActivityPlayerBinding>()

    private val trackAdapter by lazy {
        TrackAdapter(
            onTrackClick = ::onTrackClick,
            onDownloadTrack = ::onDownloadTrack,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MediaDownloadService.getComponent((applicationContext as App).getAppComponent())
            .inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        binding.tracks.apply {
            layoutManager = LinearLayoutManager(this@DownloadsActivity)
            adapter = trackAdapter
        }

        lifecycleScope.launch {
            downloadsViewModel.getTracks()
                .onEach { trackAdapter.submitList(it) }
                .flowWithLifecycle(lifecycle)
                .collect()
        }

        downloadsViewModel.downloads
            .reactWith(
                onSuccess = { downloads ->
                    downloads.map { "state: ${it.state}, id: ${it.request.id}" }
                        .onEach { Timber.d("downloadsStorage $it") }
                    Timber.d("downloadsStorage ------------------------")
                },
                onError = { }
            )
            .onEach {  }
            .flowWithLifecycle(lifecycle)
            .launchIn(lifecycleScope)

        startDownloadService()
    }

    override fun onDestroy() {
        downloadTracker.release()
        MediaDownloadService.deleteComponent()
        super.onDestroy()
    }

    private fun onTrackClick(track: Track) {
        binding.player.player?.apply {
            track.audioUri?.let { trackUri ->
                setMediaItem(
                    MediaItem.fromUri(trackUri)
                )
                prepare()
            }
        }
    }

    private fun onDownloadTrack(track: Track) {
        track.audioUri?.let { trackUri ->
            downloadTracker.toggleDownload(
                mediaItem = MediaItem.fromUri(trackUri),
                renderersFactory = downloadsUtils.buildRenderersFactory(
                    context = this,
                    preferExtensionRenderer = false,
                ),
                serviceClass = MediaDownloadService::class.java,
            )
        }
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
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT > 23) {
            binding.player.onPause()
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        binding.player.player?.apply {
            release()
        }
    }

    private fun initializePlayer(): Boolean {
        val player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build()
        binding.player.player = player
        player.playWhenReady = true
        player.seekTo(0, 0)
        player.prepare()
        return true
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