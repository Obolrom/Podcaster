package io.obolonsky.downloads.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.core.di.actions.GetDownloadServiceClassAction
import io.obolonsky.core.di.actions.StartDownloadServiceAction
import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.downloads.Downloader
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.core.di.player.PlayerDataSourceFactories
import io.obolonsky.downloads.R
import io.obolonsky.downloads.TrackAdapter
import io.obolonsky.downloads.databinding.ActivityPlayerBinding
import io.obolonsky.utils.get
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Provider

class DownloadsActivity : AppCompatActivity() {

    @Inject
    internal lateinit var downloadTracker: Downloader

    @Inject
    internal lateinit var dataSourceFactories: PlayerDataSourceFactories

    @Inject
    internal lateinit var getDownloadServiceClassAction: GetDownloadServiceClassAction

    @Inject
    internal lateinit var startDownloadServiceAction: Provider<StartDownloadServiceAction>

    private val componentViewModel by viewModels<ComponentViewModel>()

    private val downloadsViewModel by lazyViewModel { savedStateHandle ->
        componentViewModel.downloadsComponent
            .downloadsViewModelFactory()
            .create(savedStateHandle)
    }

    private val binding by viewBinding<ActivityPlayerBinding>()

    private val notificationRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {  }

    private val trackAdapter by lazy {
        TrackAdapter(
            onTrackClick = ::onTrackClick,
            onDownloadTrack = ::onDownloadTrack,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        componentViewModel.downloadsComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        binding.tracks.apply {
            layoutManager = LinearLayoutManager(this@DownloadsActivity)
            adapter = trackAdapter
        }

        downloadsViewModel.tracks
            .onEach(trackAdapter::submitList)
            .flowWithLifecycle(lifecycle)
            .launchIn(lifecycleScope)

        downloadsViewModel.load()

        startDownloadService()
    }

    override fun onDestroy() {
        downloadTracker.release()
        notificationRequest.unregister()
        super.onDestroy()
    }

    private fun onTrackClick(track: Track) {
        binding.player.player?.apply {
            track.audioUri?.let { trackUri ->
                setMediaItem(
                    MediaItem.fromUri(trackUri)
                )
            }
        }
    }

    private fun onDownloadTrack(track: Track) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        downloadTrack(track)
    }

    private fun downloadTrack(track: Track) {
        track.audioUri?.let { trackUri ->
            downloadTracker.toggleDownload(
                mediaItem = MediaItem.fromUri(trackUri),
                serviceClass = getDownloadServiceClassAction.downloadServiceClass,
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
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(dataSourceFactories.cacheDataSourceFactory)
            )
            .build()
        binding.player.player = player
        player.playWhenReady = true
        player.seekTo(0, 0)
        player.prepare()
        return true
    }

    @OptIn(markerClass = [UnstableApi::class])
    private fun startDownloadService() {
        startDownloadServiceAction.get {
            start(this@DownloadsActivity)
        }
    }

}