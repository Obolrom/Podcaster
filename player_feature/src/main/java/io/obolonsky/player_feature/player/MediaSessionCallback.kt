package io.obolonsky.player_feature.player

import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.Rating
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.ListenableFuture
import timber.log.Timber
import javax.inject.Inject

class MediaSessionCallback @Inject constructor() : MediaSession.Callback {

    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult {
        Timber.d("MediaSessionCallback onConnect")
        val connectionResult = super.onConnect(session, controller)
        val sessionCommands = connectionResult.availableSessionCommands
            .buildUpon()
            .add(SessionCommand(REWIND_30, Bundle()))
            .add(SessionCommand(FAST_FW_30, Bundle()))
            .build()

        return MediaSession.ConnectionResult.accept(
            sessionCommands,
            connectionResult.availablePlayerCommands
        )
    }

    override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
        Timber.d("MediaSessionCallback onPostConnect")
        super.onPostConnect(session, controller)
    }

    override fun onDisconnected(session: MediaSession, controller: MediaSession.ControllerInfo) {
        Timber.d("MediaSessionCallback onDisconnected")
        super.onDisconnected(session, controller)
    }

    override fun onPlayerCommandRequest(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        playerCommand: Int
    ): Int {
        Timber.d("MediaSessionCallback onPlayerCommandRequest")
        return super.onPlayerCommandRequest(session, controller, playerCommand)
    }

    override fun onSetRating(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaId: String,
        rating: Rating
    ): ListenableFuture<SessionResult> {
        Timber.d("MediaSessionCallback onSetRating")
        return super.onSetRating(session, controller, mediaId, rating)
    }

    override fun onSetRating(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        rating: Rating
    ): ListenableFuture<SessionResult> {
        Timber.d("MediaSessionCallback onSetRating 2")
        return super.onSetRating(session, controller, rating)
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
        Timber.d("MediaSessionCallback onCustomCommand")
        return super.onCustomCommand(session, controller, customCommand, args)
    }

    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>
    ): ListenableFuture<MutableList<MediaItem>> {
        Timber.d("MediaSessionCallback onAddMediaItems")
        return super.onAddMediaItems(mediaSession, controller, mediaItems)
    }

    companion object {
        const val REWIND_30 = "REWIND_30"
        const val FAST_FW_30 = "FAST_FW_30"
    }
}