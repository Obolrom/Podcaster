package io.obolonsky.podcaster.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.MediaItem
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.data.misc.handle
import io.obolonsky.podcaster.viewmodels.PlayerViewModel
import io.obolonsky.podcaster.data.room.entities.Song
import io.obolonsky.podcaster.databinding.FragmentPlayerBinding
import io.obolonsky.podcaster.ui.adapters.BaseAdapter
import io.obolonsky.podcaster.ui.adapters.SongAdapter
import io.obolonsky.podcaster.viewmodels.SongsViewModel
import kotlinx.android.synthetic.main.fragment_player.*

class PlayerFragment : AbsFragment(R.layout.fragment_player),
    BaseAdapter.OnClickItemListener<Song> {

    private val songsViewModel: SongsViewModel by viewModels { appViewModelFactory }

    private val playerViewModel: PlayerViewModel by viewModels { appViewModelFactory }

    private val musicItemsAdapter by lazy(LazyThreadSafetyMode.NONE) { SongAdapter() }

    override fun initViewModels() {
        songsViewModel.songs.handle(this, ::onDataLoaded)
        songsViewModel.loadSongs()
    }

    override fun initViews(savedInstanceState: Bundle?) {
        video_player.player = playerViewModel.player.getPlayer()

        start_button.setOnClickListener {
            if (playerViewModel.player.isPlaying)
                playerViewModel.player.pause()
            else
                playerViewModel.player.resume()
        }

        rewind.setOnClickListener {
            playerViewModel.player.rewind(15000)
        }

        forward.setOnClickListener {
            playerViewModel.player.forward(15000)
        }
    }

    private fun onDataLoaded(musicItems: List<Song>) {
        initAdapter()

        musicItemsAdapter.submitList(musicItems)
    }

    private fun initAdapter() {
        recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = musicItemsAdapter
            musicItemsAdapter.onClick = this@PlayerFragment
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerViewModel.player.release()
    }

    private fun initMP3File(uri: Uri): MediaItem {
        return MediaItem.Builder()
            .setUri(uri)
            .setMediaId("RHCP")
            .build()
    }

    override fun onItemClick(item: Song) {
        initMP3File(item.mediaUrl.toUri()).let { song ->
            playerViewModel.player.apply {
                setMediaItem(song)
                resume()
            }
        }
    }
}