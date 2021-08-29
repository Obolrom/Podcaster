package io.obolonsky.podcaster.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.exoplayer2.*
import io.obolonsky.podcaster.MusicPlayer
import io.obolonsky.podcaster.api.TestMusicLibraryApi
import io.obolonsky.podcaster.viewmodels.PlayerViewModel
import io.obolonsky.podcaster.appComponent
import io.obolonsky.podcaster.data.responses.MediaResponse
import io.obolonsky.podcaster.databinding.FragmentPlayerBinding
import io.obolonsky.podcaster.di.AppViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class PlayerFragment : Fragment() {

    @Inject
    lateinit var appViewModelFactory: AppViewModelFactory

    @Inject
    lateinit var musicLibraryApi: TestMusicLibraryApi

    private val playerViewModel: PlayerViewModel by viewModels { appViewModelFactory }

    @Inject
    lateinit var player: MusicPlayer

    private var _binding: FragmentPlayerBinding? = null
    private val binding: FragmentPlayerBinding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.videoPlayer.player = player.getPlayer()

        binding.startButton.setOnClickListener {
            if (player.isPlaying) pausePlay()
            else startPlay()
        }

        binding.rewind.setOnClickListener {
            player.apply { seekTo(currentPosition - 15000) }
        }

        binding.forward.setOnClickListener {
            player.apply { seekTo(currentPosition + 15000) }
        }

        executeApiQuery()
    }

    private fun executeApiQuery() {
        musicLibraryApi.getMusic().enqueue(object : Callback<MediaResponse> {
            override fun onResponse(call: Call<MediaResponse>, response: Response<MediaResponse>) {
                if (response.isSuccessful) {
                    val playlist = response.body()
                    playlist?.mediaItems?.getOrNull(0)?.let {
                        initMP3File(it.mediaUrl.toUri()).let { item ->
                            player.apply {
                                setMediaItem(item)
                                resume()
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<MediaResponse>, t: Throwable) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    private fun initMP3File(uri: Uri): MediaItem {
        return MediaItem.Builder()
            .setUri(uri)
            .setMediaId("RHCP")
            .build()
    }

    private fun startPlay() {
        player.resume()
    }

    private fun pausePlay() {
        player.pause()
    }
}