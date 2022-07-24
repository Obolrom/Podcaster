package io.obolonsky.player_feature.player

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.player_feature.di.DaggerPlayerComponent
import timber.log.Timber
import javax.inject.Inject

class PodcasterPlaybackService : MediaSessionService(), AudioManager.OnAudioFocusChangeListener {

    @Inject
    lateinit var podcasterPlayer: PodcasterPlayer

    private var mediaSession: MediaSession? = null

    private val audioManager by lazy {
        getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    private val audioFocusRequest by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(this)
                .build()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

    private val focusLock by lazy { Any() }

    override fun onCreate() {
        DaggerPlayerComponent.factory()
            .create((application as App).getAppComponent())
            .inject(this)
        super.onCreate()

        mediaSession = MediaSession.Builder(this, podcasterPlayer.player)
            .setCallback(MediaSessionCallback())
            .build()

        podcasterPlayer.player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (!isPlaying) return

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val focusResponse = audioManager.requestAudioFocus(audioFocusRequest)

                    synchronized(focusLock) {
                        when (focusResponse) {
                            AudioManager.AUDIOFOCUS_REQUEST_FAILED -> {
                                Timber.d("audioFocus AUDIOFOCUS_REQUEST_FAILED")
                            }

                            AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> {
                                Timber.d("audioFocus AUDIOFOCUS_REQUEST_DELAYED")
                            }

                            AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                                podcasterPlayer.play()
                                Timber.d("audioFocus AUDIOFOCUS_REQUEST_GRANTED")
                            }
                        }
                    }
                }
            }
        })
        podcasterPlayer.play()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession?.apply {
            release()
            mediaSession = null
        }
        podcasterPlayer.release()
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                podcasterPlayer.pause()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    audioManager.abandonAudioFocusRequest(audioFocusRequest)
                }
                Timber.d("audioFocus AUDIOFOCUS_LOSS")
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                Timber.d("audioFocus AUDIOFOCUS_LOSS_TRANSIENT")
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                Timber.d("audioFocus AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK")
            }

            AudioManager.AUDIOFOCUS_GAIN -> {
                podcasterPlayer.play()
                Timber.d("audioFocus AUDIOFOCUS_GAIN")
            }
        }
    }
}