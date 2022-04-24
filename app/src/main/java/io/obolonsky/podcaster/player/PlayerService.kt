package io.obolonsky.podcaster.player

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import dagger.hilt.android.AndroidEntryPoint
import io.obolonsky.podcaster.MusicPlayer
import io.obolonsky.podcaster.player.Constants.MEDIA_ROOT_ID
import io.obolonsky.podcaster.player.Constants.NETWORK_ERROR
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class PlayerService : MediaBrowserServiceCompat() {

    @Inject
    lateinit var dataSourceFactory: DefaultDataSourceFactory

    @Inject
    lateinit var musicPlayer: MusicPlayer

//    @Inject
//    lateinit var exoPlayer: SimpleExoPlayer

    @Inject
    lateinit var musicDataSource: MusicDataSource

    var isForegroundService = false
    private var isPlayerInitialized = false

    private lateinit var notificationManager: MusicNotificationManager

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private var curPlayingSong: MediaMetadataCompat? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var musicPlayerListener: Player.Listener

    override fun onCreate() {
        super.onCreate()
        serviceScope.launch(Dispatchers.IO) {
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
        }

        musicPlayerListener = MusicPlayerListener(this)
        musicPlayer.addListener(musicPlayerListener)
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
//                val resultsSent = musicDataSource.whenReady { isInitialized ->
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
//                }
                if(!resultsSent) {
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
        val curSongIndex = if(curPlayingSong == null) 0 else songs.indexOf(itemToPlay)
        musicPlayer.addMediaSource(musicDataSource.asMediaSource(dataSourceFactory))
        musicPlayer.resume()
//        exoPlayer.playWhenReady = playNow
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        musicPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        musicPlayer.freeUpResourcesAndRelease()
    }

    private inner class MusicQueueNavigator : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(
            player: Player,
            windowIndex: Int
        ): MediaDescriptionCompat {
            return musicDataSource.songs[0/*windowIndex*/].description
        }
    }

    companion object {
        private const val NOTIFICATION_REQUEST_CODE = 0
        private const val SERVICE_TAG = "PlayerService"

        fun getPendingIntentFlags(): Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        }
    }
}