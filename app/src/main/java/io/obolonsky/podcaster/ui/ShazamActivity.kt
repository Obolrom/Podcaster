package io.obolonsky.podcaster.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.podcaster.PodcasterApp
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.databinding.ActivityShazamBinding
import io.obolonsky.podcaster.misc.appComponent
import io.obolonsky.podcaster.misc.launchWhenStarted
import io.obolonsky.podcaster.viewmodels.SongsViewModel
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class ShazamActivity : AppCompatActivity() {

    private val songsViewModel: SongsViewModel by lazyViewModel {
        appComponent.songsViewModel().create(it)
    }

    // TODO: just for test
    @Inject
    lateinit var player: SimpleExoPlayer

    private val binding: ActivityShazamBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as PodcasterApp).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shazam)

        initViewModel()
        initViews()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            songsViewModel.shazamDetect
                .onEach { detected ->
                    player.prepare()
                    player.playWhenReady = true
                    detected.track?.audioUri?.let { audioUri ->
                        player.setMediaItem(MediaItem.fromUri(audioUri))
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

    private fun initViews() {
        binding.shazam.setOnClickListener {
            val recordIntent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
            startActivityForResult(recordIntent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            data?.data?.let { uri ->
                player.prepare()
                player.playWhenReady = true
                player.setMediaItem(MediaItem.fromUri(uri))
                lifecycleScope.launchWhenStarted {
                    val bytes = contentResolver
                        ?.openInputStream(uri)
                        ?.readBytes()

                    bytes?.let(File(filesDir, "fileToDetect.mp3")::writeBytes)
                    val file = File(filesDir, "fileToDetect.mp3")
                    songsViewModel.audioDetect(file)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1234 -> {
                if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED)
                    0
                else
                    1
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}