package io.obolonsky.podcaster.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.player_feature.AudioSource
import io.obolonsky.podcaster.PodcasterApp
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.data.misc.toaster
import io.obolonsky.podcaster.databinding.ActivityShazamBinding
import io.obolonsky.podcaster.misc.appComponent
import io.obolonsky.podcaster.misc.launchWhenStarted
import io.obolonsky.podcaster.viewmodels.SongsViewModel
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

class ShazamActivity : AppCompatActivity() {

    private val songsViewModel: SongsViewModel by lazyViewModel {
        appComponent.songsViewModel().create(it)
    }

    private val binding: ActivityShazamBinding by viewBinding()

    private val defaultMediaRecorder = DefaultMediaRecorder(
        activityResultRegistry = activityResultRegistry,
        onAudioUriCallback = ::onAudioUri
    )

    private val toaster by toaster()

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
            songsViewModel.shazamDetect
                .onEach { detected ->
                    detected.track?.audioUri?.let { audioUri ->
                        AudioSource.setMediaUri(audioUri)
                        showPlayer()
                    }

                    detected.track?.imageUrls?.firstOrNull()?.let { imageUrl ->
                        binding.image.load(imageUrl) {
                            crossfade(500)
                        }
                    }
                    detected.track?.title?.let { binding.title.text = it }
                    detected.track?.subtitle?.let { binding.subtitle.text = it }
                }
                .launchWhenStarted(lifecycleScope)
        }
    }

    private fun showPlayer() {
        binding.shazam.visibility = View.GONE

        supportFragmentManager.commit {
            add(R.id.container, NewPlayerFragment())
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
                toaster.showToast(this@ShazamActivity, "intent not handled")
            }
        }
    }

    private fun initViews() {
        binding.shazam.setOnClickListener {
            defaultMediaRecorder.recordAudio()
        }
    }

    private fun onAudioUri(audioUri: Uri) {
        @Suppress("BlockingMethodInNonBlockingContext")
        val bytes = contentResolver
            ?.openInputStream(audioUri)
            ?.readBytes()

        bytes?.let(File(filesDir, RECORDED_AUDIO_FILENAME)::writeBytes)
        val file = File(filesDir, RECORDED_AUDIO_FILENAME)
        songsViewModel.audioDetect(file)
        binding.image.load(audioUri)
    }

    private companion object {
        const val RECORDED_AUDIO_FILENAME = "fileToDetect.mp3"
    }
}