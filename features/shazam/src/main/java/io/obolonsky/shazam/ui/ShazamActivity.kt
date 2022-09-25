package io.obolonsky.shazam.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.core.di.actions.NavigateToDownloadsAction
import io.obolonsky.core.di.actions.CreatePlayerScreenAction
import io.obolonsky.core.di.actions.StopPlayerService
import io.obolonsky.core.di.common.AudioSource
import io.obolonsky.core.di.common.launchWhenStarted
import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.core.di.toaster
import io.obolonsky.shazam.R
import io.obolonsky.shazam.databinding.ActivityShazamBinding
import io.obolonsky.shazam.viewmodels.ComponentViewModel
import io.obolonsky.shazam.viewmodels.ShazamViewModel
import io.obolonsky.utils.get
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider

class ShazamActivity : AppCompatActivity() {

    @Inject
    internal lateinit var createPlayerScreenAction: Provider<CreatePlayerScreenAction>

    @Inject
    internal lateinit var stopPlayerServiceAction: Provider<StopPlayerService>

    @Inject
    internal lateinit var navigateToDownloadsAction: NavigateToDownloadsAction

    private val componentViewModel by viewModels<ComponentViewModel>()

    private val trackAdapter by lazy {
        TrackAdapter(
            onTrackClick = ::onTrackDetected,
            onRemoveTrack = shazamViewModel::deleteRecentTrack,
        )
    }

    private val shazamViewModel: ShazamViewModel by lazyViewModel {
        componentViewModel.shazamComponent
            .shazamViewModel()
            .create(it)
    }

    private val binding: ActivityShazamBinding by viewBinding()

    private val toaster by toaster()

    private val recordPermission = MediaRecorderPermission(
        activityResultRegistry = activityResultRegistry,
        onPermissionGranted = ::onRecordPermissionGranted,
    )

    private val mediaRecorderViewModel by lazyViewModel {
        componentViewModel.shazamComponent
            .recorderViewModel()
            .create(
                savedStateHandle = it,
                outputFile = File(filesDir, RECORDED_AUDIO_FILENAME),
                recordDurationMs = TimeUnit.SECONDS.toMillis(7),
            )
    }

    private var isPlayerShown = false

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.handleIntent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        componentViewModel.shazamComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shazam)

        savedInstanceState?.getBoolean(PLAYER_IS_PLAYING_KEY)?.let { isPlayerRunning ->
            isPlayerShown = isPlayerRunning
            binding.shazam.isVisible = !isPlayerRunning
        }

        binding.recentTracks.apply {
            adapter = trackAdapter
            layoutManager = LinearLayoutManager(
                this@ShazamActivity,
                LinearLayoutManager.VERTICAL,
                true
            )
        }

        intent?.handleIntent()

        initViewModel()
        initViews()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(PLAYER_IS_PLAYING_KEY, isPlayerShown)
        super.onSaveInstanceState(outState)
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            mediaRecorderViewModel.audioRecordingComplete
                .onEach { onMediaRecorded() }
                .launchWhenStarted(lifecycleScope)

            shazamViewModel.shazamDetect
                .mapNotNull { it.track }
                .onEach(::onTrackDetected)
                .launchWhenStarted(lifecycleScope)
//                .flowWithLifecycle(lifecycle)
//                .collect()
        }

        shazamViewModel.getRecentShazamTracks()
            .onEach(::onRecentTracks)
            .flowWithLifecycle(lifecycle)
            .launchIn(lifecycleScope)
    }

    private fun Track.metadata(): MediaMetadata {
        return MediaMetadata.Builder()
            .setArtworkUri(imageUrls.firstOrNull()?.toUri())
            .setDisplayTitle(title)
            .setTitle(title)
            .apply {
                setExtras(
                    bundleOf(
                        "mediaUrl" to audioUri,
                        "shazam_images" to imageUrls.take(2)
                    )
                )
            }
            .setArtist(subtitle)
            .build()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
            stopPlayerServiceAction.get {
                stop(this@ShazamActivity)
            }
            isPlayerShown = false
            binding.shazam.visibility = View.VISIBLE
            AudioSource.clear()
        } else {
            super.onBackPressed()
        }
    }

    private fun showPlayer() {
        if (isPlayerShown) return
        isPlayerShown = true

        binding.shazam.visibility = View.GONE

        createPlayerScreenAction.get {
            supportFragmentManager.commit {
                addToBackStack(null)
                add(R.id.container, showPlayer())
            }
        }
    }

    private fun Intent.handleIntent() {
        when (action) {
            // When the BII is matched, Intent.Action_VIEW will be used
            Intent.ACTION_VIEW -> {
                toaster.showToast(this@ShazamActivity, "handle intent")
            }

            // Otherwise start the app as you would normally do.
            else -> {
//                toaster.showToast(this@ShazamActivity, "intent not handled")
            }
        }
    }

    private fun initViews() {
        binding.shazam.setOnClickListener {
            it.isEnabled = false
            recordPermission.requestRecordPermission()
        }
    }

    private fun onTrackDetected(track: Track) {
        track.audioUri?.let { audioUri ->
            AudioSource.addTrack(track)
            AudioSource.addMediaItem(
                MediaItem.Builder()
                    .setRequestMetadata(
                        MediaItem.RequestMetadata.Builder()
                            .setMediaUri(audioUri.toUri())
                            .build()
                    )
                    .setUri(audioUri)
                    .setMediaMetadata(track.metadata())
                    .build()
            )
        }
        track.relatedTracks.onEach { relatedTrack ->
            AudioSource.addTrack(relatedTrack)
            relatedTrack.audioUri?.let { audioUri ->
                AudioSource.addMediaItem(
                    MediaItem.Builder()
                        .setRequestMetadata(
                            MediaItem.RequestMetadata.Builder()
                                .setMediaUri(audioUri.toUri())
                                .build()
                        )
                        .setUri(audioUri)
                        .setMediaMetadata(relatedTrack.metadata())
                        .build()
                )
            }
        }
        showPlayer()
    }

    private fun onRecordPermissionGranted() {
        mediaRecorderViewModel.record()
    }

    private fun onRecentTracks(tracks: List<Track>) {
        trackAdapter.submitList(tracks)
    }

    private fun onMediaRecorded() {
        binding.shazam.isEnabled = true
        val file = File(filesDir, RECORDED_AUDIO_FILENAME)
        shazamViewModel.audioDetect(file)
    }

    private companion object {
        const val PLAYER_IS_PLAYING_KEY = "PLAYER_IS_PLAYING_KEY"
        const val RECORDED_AUDIO_FILENAME = "fileToDetect.mp3"
    }
}