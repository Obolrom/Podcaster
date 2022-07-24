package io.obolonsky.player_feature.player

import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.player_feature.di.DaggerPlayerComponent
import javax.inject.Inject

class PodcasterPlaybackService : MediaSessionService() {

    @Inject
    lateinit var podcasterPlayer: PodcasterPlayer

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        DaggerPlayerComponent.factory()
            .create((application as App).getAppComponent())
            .inject(this)
        super.onCreate()

        mediaSession = MediaSession.Builder(this, podcasterPlayer.player)
            .build()

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
}