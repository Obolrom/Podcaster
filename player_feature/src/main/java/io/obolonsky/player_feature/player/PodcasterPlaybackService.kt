package io.obolonsky.player_feature.player

import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.Rating
import androidx.media3.session.*
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.player_feature.di.DaggerPlayerComponent
import timber.log.Timber
import javax.inject.Inject

class PodcasterPlaybackService : MediaSessionService() {

    @Inject
    lateinit var podcasterPlayer: PodcasterPlayer

    private val mediaSession: MediaSession by lazy {
        MediaSession.Builder(this, podcasterPlayer.player)
            .setCallback(MediaSessionCallback())
            .build()
    }

    private val customCommands by lazy {
        listOf(
            getForwardCommandButton(SessionCommand(FAST_FW_30, Bundle.EMPTY)),
            getRewindCommandButton(SessionCommand(REWIND_30, Bundle.EMPTY)),
        )
    }

    private var customLayout = ImmutableList.of<CommandButton>()

    override fun onCreate() {
        inject()
        super.onCreate()

        customLayout = ImmutableList.of(customCommands[0], customCommands[1])
//        setMediaNotificationProvider()

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

    private inner class MediaSessionCallback : MediaSession.Callback {

        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            Timber.d("MediaSessionCallback onConnect")
            val connectionResult = super.onConnect(session, controller)
            val sessionCommands = connectionResult.availableSessionCommands.buildUpon()
            customCommands.forEach { command ->
                command.sessionCommand?.let { sessionCommands.add(it) }
            }

            return MediaSession.ConnectionResult.accept(
                sessionCommands.build(),
                connectionResult.availablePlayerCommands
            )
        }

        override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
            Timber.d("MediaSessionCallback onPostConnect")
            if (customLayout.isNotEmpty() && controller.controllerVersion != 0) {
                mediaSession.setCustomLayout(controller, customLayout)
            }
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
            when (customCommand.customAction) {
                FAST_FW_30 -> {
                    podcasterPlayer.forward()
                }

                REWIND_30 -> {
                    podcasterPlayer.rewind()
                }
            }

            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        }

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): ListenableFuture<MutableList<MediaItem>> {
            Timber.d("MediaSessionCallback onAddMediaItems")
            return super.onAddMediaItems(mediaSession, controller, mediaItems)
        }
    }

    companion object {
        const val REWIND_30 = "REWIND_30"
        const val FAST_FW_30 = "FAST_FW_30"
    }
}