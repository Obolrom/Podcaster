package io.obolonsky.podcaster

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import androidx.core.net.toFile
import androidx.core.os.EnvironmentCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView

    private var player: ExoPlayer? = null

    private val playbackStateListener = PlaybackStateListener()

    private lateinit var file: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.video_player)

        val files = assets?.list("media")
        file = Uri.parse("asset:///media/${files?.getOrNull(0)!!}")
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initPlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || player == null) {
            initPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun initMP3File(uri: Uri) {
        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .setMediaId("otherSide")
            .build()

        player?.addMediaItem(mediaItem)
    }

    private fun initPlayer() {
        if (player == null) {
            val trackSelector = DefaultTrackSelector(this)
            trackSelector.setParameters(
                trackSelector
                    .buildUponParameters()
                    .setMaxVideoSizeSd())
            player = SimpleExoPlayer.Builder(this)
                .setTrackSelector(trackSelector)
                .build()
        }

        playerView.player = player
        player?.addListener(playbackStateListener)
        initMP3File(file)
        startPlay()
    }

    private fun releasePlayer() {
        player?.apply {
            removeListener(playbackStateListener)
            release()
        }
        player = null
    }

    private fun startPlay() {
        player?.apply {
            playWhenReady = playWhenReady
            seekTo(0)
            prepare()
            play()
        }
    }

    private inner class PlaybackStateListener(): Player.Listener {

        override fun onPlaybackStateChanged(state: Int) {
            super.onPlaybackStateChanged(state)
            val stateString = when (state) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Log.d("playerState", "changed state to $stateString")
        }
    }
}