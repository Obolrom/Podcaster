package io.obolonsky.player_feature.player

import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.player_feature.di.DaggerPlayerComponent
import javax.inject.Inject

class PodcasterPlaybackService : MediaSessionService() {

    @Inject
    lateinit var podcasterPlayer: PodcasterPlayer

    @Inject
    lateinit var mediaSession: MediaSession

    override fun onCreate() {
        inject()
        super.onCreate()

        podcasterPlayer.play()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }

    override fun onDestroy() {
        super.onDestroy()
        podcasterPlayer.release()
        mediaSession.release()
    }

    private fun inject() {
        DaggerPlayerComponent.factory()
            .create((application as App).getAppComponent())
            .inject(this)
    }
}