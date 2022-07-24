package io.obolonsky.player_feature

import android.content.ComponentName
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import io.obolonsky.coreui.BaseFragment
import io.obolonsky.player_feature.databinding.FragmentPlayerBinding
import io.obolonsky.player_feature.databinding.FragmentPlayerNavigationBinding
import io.obolonsky.player_feature.player.PodcasterPlaybackService

class PlayerFragment : BaseFragment(R.layout.fragment_player) {

    private val binding by viewBinding<FragmentPlayerBinding>()
    private val playerNavBinding: FragmentPlayerNavigationBinding by
        viewBinding(viewBindingRootId = R.id.player_navigation)

    private val sessionToken by lazy {
        SessionToken(
            requireContext(),
            ComponentName(requireContext(), PodcasterPlaybackService::class.java)
        )
    }

    private val playerListener by lazy {
        object : Player.Listener {

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                val trackTitle = mediaMetadata.displayTitle
                    ?: mediaMetadata.title
                    ?: mediaMetadata.albumTitle
                playerNavBinding.audioTrackTitle.text = trackTitle
            }
        }
    }

    private var controllerFuture: ListenableFuture<MediaController>? = null

    override fun initViewModels() { }

    override fun initViews(savedInstanceState: Bundle?) {
        activity?.window?.navigationBarColor = ContextCompat.getColor(
            requireContext(),
            R.color.pink_red
        )
        binding.playerView.showController()
    }

    override fun onStart() {
        super.onStart()
        controllerFuture = MediaController.Builder(requireContext(), sessionToken)
            .buildAsync()
        controllerFuture?.addListener({
            val player = controllerFuture?.get()
            binding.playerView.player = player
            player?.addListener(playerListener)
        }, MoreExecutors.directExecutor())
    }

    override fun onStop() {
        super.onStop()
        controllerFuture?.let(MediaController::releaseFuture)
        binding.playerView.player?.removeListener(playerListener)
    }
}