package io.obolonsky.podcaster.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.player_feature.AudioSource
import io.obolonsky.player_feature.PlayerFragment
import io.obolonsky.player_feature.player.PodcasterPlaybackService
import io.obolonsky.podcaster.PodcasterApp
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.data.misc.toaster
import io.obolonsky.podcaster.databinding.ActivityShazamBinding
import io.obolonsky.podcaster.misc.appComponent
import io.obolonsky.podcaster.misc.launchWhenStarted
import io.obolonsky.podcaster.viewmodels.ShazamViewModel
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit

class ShazamActivity : AppCompatActivity() {

    private val shazamViewModel: ShazamViewModel by lazyViewModel {
        appComponent.shazamViewModel().create(it)
    }

    private val binding: ActivityShazamBinding by viewBinding()

    private val toaster by toaster()

    private val recordPermission = MediaRecorderPermission(
        activityResultRegistry = activityResultRegistry,
        onPermissionGranted = ::onRecordPermissionGranted,
    )

    private val mediaRecorder by lazy {
        MediaRecorder(
            outputFile = File(filesDir, RECORDED_AUDIO_FILENAME),
            recordDurationMs = TimeUnit.SECONDS.toMillis(7),
            lifecycleScope = lifecycleScope,
            onMediaRecorded = ::onMediaRecorded,
        )
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.handleIntent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as PodcasterApp).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shazam)

        intent?.handleIntent()

        initViewModel()
        initViews()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            shazamViewModel.shazamDetect
                .mapNotNull { it.track }
                .onEach { track ->
                    track.audioUri?.let { audioUri ->
                        AudioSource.addMediaItem(
                            MediaItem.Builder()
                                .setUri(audioUri)
                                .setMediaMetadata(track.metadata())
                                .build()
                        )
                    }
                    track.relatedTracks.onEach { relatedTrack ->
                        relatedTrack.audioUri?.let { audioUri ->
                            AudioSource.addMediaItem(
                                MediaItem.Builder()
                                    .setUri(audioUri)
                                    .setMediaMetadata(relatedTrack.metadata())
                                    .build()
                            )
                        }
                        showPlayer()
                    }

                    track.imageUrls.firstOrNull()?.let { imageUrl ->
                        binding.image.load(imageUrl) {
                            crossfade(500)
                        }
                    }
                    track.title?.let { binding.title.text = it }
                    track.subtitle?.let { binding.subtitle.text = it }
                }
                .launchWhenStarted(lifecycleScope)
//                .flowWithLifecycle(lifecycle)
//                .collect()
        }
    }

    private fun Track.metadata(): MediaMetadata {
        return MediaMetadata.Builder()
            .setArtworkUri(imageUrls.firstOrNull()?.toUri())
            .setDisplayTitle(title)
            .setTitle(title)
            .setArtist(subtitle)
            .build()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
            stopService(Intent(this, PodcasterPlaybackService::class.java))
            isPlayerShown = false
            binding.shazam.visibility = View.VISIBLE
            AudioSource.clear()
        } else {
            super.onBackPressed()
        }
    }

    private var isPlayerShown = false

    private fun showPlayer() {
        if (isPlayerShown) return
        isPlayerShown = true

        binding.shazam.visibility = View.GONE

        supportFragmentManager.commit {
            addToBackStack(null)
            add(R.id.container, PlayerFragment())
        }
    }

    private fun Intent.handleIntent() {
        when (action) {
            // When the BII is matched, Intent.Action_VIEW will be used
            Intent.ACTION_VIEW -> {
                toaster.showToast(this@ShazamActivity, "handle intent")
                Timber.d("fuckingAssistant data: $data, extras: ${extras?.getString("shazamKey")}")
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

    private fun onRecordPermissionGranted() {
        mediaRecorder.startRecording()
    }

    private fun onMediaRecorded() {
        binding.shazam.isEnabled = true
        val file = File(filesDir, RECORDED_AUDIO_FILENAME)
        shazamViewModel.audioDetect(file)
    }

    private companion object {
        const val RECORDED_AUDIO_FILENAME = "fileToDetect.mp3"
    }
}