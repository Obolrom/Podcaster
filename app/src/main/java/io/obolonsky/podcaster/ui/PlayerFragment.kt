package io.obolonsky.podcaster.ui

import android.os.Bundle
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import coil.load
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import dagger.hilt.android.AndroidEntryPoint
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.data.misc.handle
import io.obolonsky.podcaster.data.room.entities.Song
import io.obolonsky.podcaster.ui.adapters.BaseAdapter
import io.obolonsky.podcaster.ui.adapters.SongAdapter
import io.obolonsky.podcaster.viewmodels.SongsViewModel
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.fragment_player_navigation.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class PlayerFragment : AbsFragment(R.layout.fragment_player),
    BaseAdapter.OnClickItemListener<Song> {

    private val songsViewModel: SongsViewModel by viewModels()

    private val musicItemsAdapter by lazy(LazyThreadSafetyMode.NONE) { SongAdapter() }

    private val speedArray by lazy {
        listOf(
            1.0f,
            1.5f,
            2.0f,
            0.8f,
        )
    }

    private val rewindTime by lazy {
        resources.getInteger(R.integer.player_rewind_time) * TimeUnit.SECONDS.toMillis(1)
    }

    private var currentSpeedPosition = 0

    private val List<Float>.next: Float
        get() {
            ++currentSpeedPosition
            if (currentSpeedPosition == speedArray.size) currentSpeedPosition = 0
            return this[currentSpeedPosition]
        }

    override fun initViewModels() {
        songsViewModel.songs.handle(this, ::onDataLoaded)
    }

    override fun initViews(savedInstanceState: Bundle?) {
//        exo_player.player = playerViewModel.player.getPlayer()

        exo_player.player?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                val imageResource =
                    if (isPlaying) R.drawable.ic_round_pause
                    else R.drawable.ic_round_play_arrow
                exo_play_pause.setImageResource(imageResource)
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                exo_playback_speed.setSpeedText("${playbackParameters.speed}x")
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                audio_track_title.text = mediaMetadata.title
                audio_image.load("https://upload.wikimedia.org/wikipedia/ru/c/c9/Red_hot_chili_peppers_otherside.jpg") {
                    crossfade(500)
                }
            }
        })

        exo_play_pause.setOnClickListener {
           /* playerViewModel.player.apply {
                if (isPlaying) pause()
                else resume()
            }*/
        }

        exo_forward.setOnClickListener {
//            playerViewModel.player.forward(rewindTime)
        }

        exo_rewind.setOnClickListener {
//            playerViewModel.player.rewind(rewindTime)
        }

        exo_playback_speed.setOnClickListener {
//            playerViewModel.player
//                .getPlayer()
//                .playbackParameters = PlaybackParameters(speedArray.next)
        }

        songsViewModel.loadSongList()
    }

    private fun onDataLoaded(musicItems: List<Song>) {
        musicItemsAdapter.submitList(musicItems)
        /*playerViewModel.player.getPlayer()
            .setMediaItems(musicItems.map(::initMP3File), 0, 0)
            .also { playerViewModel.resume() }*/
    }

    override fun onDestroy() {
        super.onDestroy()
//        playerViewModel.player.release()
    }

    private fun initMP3File(song: Song): MediaItem {
        return MediaItem.Builder()
            .setUri(song.mediaUrl.toUri())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .build()
            )
            .build()
    }

    override fun onItemClick(item: Song) {
//        play(item)
    }

    private fun play(song: Song) {
        initMP3File(song).let {
            /*playerViewModel.player.apply {
                setMediaItem(it)
                resume()
            }*/
        }
    }
}