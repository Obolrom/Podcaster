package io.obolonsky.podcaster.player

import android.app.PendingIntent
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import io.obolonsky.podcaster.MusicPlayer
import io.obolonsky.podcaster.PodcasterApp
import io.obolonsky.podcaster.data.repositories.SongsRepository
import io.obolonsky.podcaster.di.modules.CoroutineSchedulers
import io.obolonsky.podcaster.player.Constants.MEDIA_ROOT_ID
import io.obolonsky.podcaster.player.Constants.NETWORK_ERROR
import kotlinx.coroutines.*
import javax.inject.Inject

class PlayerService : MediaBrowserServiceCompat() {

    @Inject
    lateinit var dispatchers: CoroutineSchedulers

    @Inject
    lateinit var songsRepository: SongsRepository

    @Inject
    lateinit var dataSourceFactory: DefaultDataSourceFactory

    @Inject
    lateinit var musicPlayer: MusicPlayer

    @Inject
    lateinit var musicDataSource: MusicDataSource

    var isForegroundService = false
    private var isPlayerInitialized = false

    private lateinit var notificationManager: MusicNotificationManager

    private val serviceScope = MainScope()

    private var curPlayingSong: MediaMetadataCompat? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var musicPlayerListener: Player.Listener
    private val playerCommandReceiver by lazy { GetPlayerCommandReceiver() }

    override fun onCreate() {
        (application as PodcasterApp).appComponent.inject(this)
        super.onCreate()

        serviceScope.launch(dispatchers.io) {
            musicDataSource.fetch()
        }

        val activityIntent = packageManager
            ?.getLaunchIntentForPackage(packageName)
            ?.let { intent ->
                PendingIntent.getActivity(
                    this,
                    NOTIFICATION_REQUEST_CODE, intent,
                    getPendingIntentFlags()
                )
            }

        mediaSession = MediaSessionCompat(this, SERVICE_TAG).apply {
            setSessionActivity(activityIntent)
            isActive = true
        }

        sessionToken = mediaSession.sessionToken

        notificationManager = MusicNotificationManager(
            this,
            mediaSession.sessionToken,
            PlayerNotificationListener(this)
        ) {

        }

        val musicPlaybackPreparer = MusicPlaybackPreparer(musicDataSource) {
            curPlayingSong = it
            preparePlayer(musicDataSource.songs, it)
        }

        mediaSessionConnector = MediaSessionConnector(mediaSession).apply {
            setPlaybackPreparer(musicPlaybackPreparer)
            setQueueNavigator(MusicQueueNavigator())
            setPlayer(musicPlayer.getPlayer())
            registerCustomCommandReceiver(playerCommandReceiver)
        }

        musicPlayerListener = MusicPlayerListener(this)
        musicPlayer.addListener(musicPlayerListener)
        musicPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (!isPlaying) {
                    val currentChapter = musicDataSource
                        .chapters[musicPlayer.currentWindow]
                    val progress = musicPlayer.currentPosition

                    serviceScope.launch(dispatchers.io) {
                        songsRepository.saveAuditionProgress(
                            bookId = currentChapter.bookId,
                            chapterId = currentChapter.id,
                            progress = progress,
                        )
                    }
                }
            }
        })
        notificationManager.showNotification(musicPlayer.getPlayer())
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        when(parentId) {
            MEDIA_ROOT_ID -> {
                val resultsSent = true
                if (true /*isPlayerInitialized*/) {
                    result.sendResult(musicDataSource.asMediaItems())
                    if(!isPlayerInitialized && musicDataSource.songs.isNotEmpty()) {
                        preparePlayer(
                            songs = musicDataSource.songs,
                            itemToPlay = musicDataSource.songs[0],
                            playNow = false
                        )
                        isPlayerInitialized = true
                    }
                } else {
                    mediaSession.sendSessionEvent(NETWORK_ERROR, null)
                    result.sendResult(null)
                }
                if (!resultsSent) {
                    result.detach()
                }
            }
        }
    }

    private fun preparePlayer(
        songs: List<MediaMetadataCompat>,
        itemToPlay: MediaMetadataCompat?,
        playNow: Boolean = true,
    ) {
        musicPlayer.addMediaSource(musicDataSource.asMediaSource(dataSourceFactory))
        musicPlayer.resume()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        musicPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        mediaSessionConnector.unregisterCustomCommandReceiver(playerCommandReceiver)
        musicPlayer.freeUpResourcesAndRelease()
    }

    private inner class MusicQueueNavigator : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(
            player: Player,
            windowIndex: Int
        ): MediaDescriptionCompat {
            return musicDataSource.songs[windowIndex].description
        }
    }

    private inner class GetPlayerCommandReceiver: MediaSessionConnector.CommandReceiver {
        override fun onCommand(
            player: Player,
            controlDispatcher: ControlDispatcher,
            command: String,
            extras: Bundle?,
            cb: ResultReceiver?
        ): Boolean {
            if (command != GET_PLAYER_COMMAND) {
                return false
            }

            val bundle = Bundle()
            bundle.putBinder(MUSIC_SERVICE_BINDER_KEY, MusicServiceBinder())
            cb?.send(0, bundle)
            return true
        }
    }

    inner class MusicServiceBinder : Binder() {
        fun getExoPlayer() = musicPlayer
    }

    companion object {
        private const val NOTIFICATION_REQUEST_CODE = 0
        private const val SERVICE_TAG = "PlayerService"
        const val GET_PLAYER_COMMAND = "getPlayer"
        const val MUSIC_SERVICE_BINDER_KEY = "MusicServiceBinder"

        fun getPendingIntentFlags(): Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        }
    }
}