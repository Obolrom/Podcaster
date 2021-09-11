package io.obolonsky.podcaster.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.*
import io.obolonsky.podcaster.viewmodels.PlayerViewModel
import io.obolonsky.podcaster.data.room.Resource
import io.obolonsky.podcaster.data.room.entities.Song
import io.obolonsky.podcaster.databinding.FragmentPlayerBinding
import io.obolonsky.podcaster.ui.adapters.BaseAdapter
import io.obolonsky.podcaster.ui.adapters.SongAdapter
import io.obolonsky.podcaster.viewmodels.SongsViewModel
import timber.log.Timber

class PlayerFragment : AbsFragment(), BaseAdapter.OnClickItemListener<Song> {

    private val songsViewModel: SongsViewModel by viewModels { appViewModelFactory }

    private val playerViewModel: PlayerViewModel by viewModels { appViewModelFactory }

    private val musicItemsAdapter = SongAdapter()

    private var _binding: FragmentPlayerBinding? = null
    private val binding: FragmentPlayerBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun initViewModels() {
        songsViewModel.loadSongs().observe(viewLifecycleOwner) {
            it?.let { items -> onDataLoaded(items) }
        }
    }

    override fun initViews(savedInstanceState: Bundle?) {
        binding.videoPlayer.player = playerViewModel.player.getPlayer()

        binding.startButton.setOnClickListener {
            if (playerViewModel.player.isPlaying)
                playerViewModel.player.pause()
            else
                playerViewModel.player.resume()
        }

        binding.rewind.setOnClickListener {
            playerViewModel.player.rewind(15000)
        }

        binding.forward.setOnClickListener {
            playerViewModel.player.forward(15000)
        }
    }

    private fun onDataLoaded(musicItems: Resource<List<Song>>) {
        initAdapter()

        musicItemsAdapter.apply {
            when (musicItems) {
                is Resource.Loading -> {
                    Timber.d("fuck loading")
                }
                is Resource.Success -> {
                    submitList(musicItems.data)
                }
                is Resource.Error -> {
                    submitList(musicItems.data)
                    Toast.makeText(
                        requireContext(),
                        "data error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun initAdapter() {
        binding.recyclerView.apply {
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