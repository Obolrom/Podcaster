package io.obolonsky.player

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.*
import by.kirich1409.viewbindingdelegate.viewBinding
import com.codeboy.pager2_transformers.Pager2_ZoomOutTransformer
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import io.obolonsky.core.di.actions.StartDownloadServiceAction
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.coreui.BaseFragment
import io.obolonsky.player.databinding.FragmentPlayerBinding
import io.obolonsky.player.databinding.FragmentPlayerNavigationBinding
import io.obolonsky.player.di.PlayerComponent
import io.obolonsky.player.player.PodcasterPlaybackService
import io.obolonsky.player.player.PodcasterPlaybackService.Companion.getPlayerComponent
import io.obolonsky.player.ui.ImagesAdapter
import io.obolonsky.utils.get
import timber.log.Timber
import java.util.concurrent.ExecutionException
import javax.inject.Inject
import javax.inject.Provider
import io.obolonsky.coreui.R as CoreUiR

class PlayerFragment : BaseFragment(R.layout.fragment_player) {

    @Inject
    internal lateinit var startDownloadServiceAction: Provider<StartDownloadServiceAction>

    private val downloadViewModel by lazyViewModel {
        getComponent()
            .downloadViewModelFactory()
            .create(it)
    }

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
                onMediaMetadata(mediaMetadata)
            }
        }
    }

    private val mediaImagesAdapter by lazy { ImagesAdapter() }

    private var controllerFuture: ListenableFuture<MediaController>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun initViewModels() { }

    override fun initViews(savedInstanceState: Bundle?) {
        activity?.window?.navigationBarColor = ContextCompat.getColor(
            requireContext(),
            CoreUiR.color.pink_red
        )
        binding.playerView.showController()
        playerNavBinding.images.setPageTransformer(Pager2_ZoomOutTransformer())
        playerNavBinding.images.adapter = mediaImagesAdapter

        playerNavBinding.download.setOnClickListener {
            binding.playerView.player
                ?.currentMediaItemIndex
                ?.let(downloadViewModel::download)
        }

        startDownloadServiceAction.get {
            start(requireContext())
        }
    }

    override fun onStart() {
        super.onStart()
        controllerFuture = MediaController.Builder(requireContext(), sessionToken)
            .setListener(MediaControllerListener())
            .buildAsync()

        controllerFuture?.addListener({
            try {
                val player = controllerFuture?.get()
                binding.playerView.player = player
                player?.addListener(playerListener)
                player?.mediaMetadata?.let(::onMediaMetadata)
            } catch (e: ExecutionException) {
                e.printStackTrace()
                Timber.e(e)
                if (e.cause is SecurityException) {
                    Timber.e("SecurityException at controllerFuture.addListener ${e.cause}")
                    // The session rejected the connection.
                }
            }
        }, MoreExecutors.directExecutor())
    }

    override fun onStop() {
        super.onStop()
        controllerFuture?.let(MediaController::releaseFuture)
        binding.playerView.player?.removeListener(playerListener)
    }

    private fun getComponent(): PlayerComponent {
        return getPlayerComponent(
            applicationProvider = (activity?.applicationContext as App).getAppComponent()
        )
    }

    private fun onMediaMetadata(mediaMetadata: MediaMetadata) {
        val trackTitle = mediaMetadata.displayTitle
            ?: mediaMetadata.title
            ?: mediaMetadata.albumTitle
        playerNavBinding.audioTrackTitle.text = trackTitle
        mediaMetadata.extras
            ?.getStringArrayList("shazam_images")
            ?.let(mediaImagesAdapter::submitList)
    }

    inner class MediaControllerListener : MediaController.Listener {

        override fun onDisconnected(controller: MediaController) {
            Timber.d("MediaControllerListener onDisconnected")
            super.onDisconnected(controller)
        }

        override fun onSetCustomLayout(
            controller: MediaController,
            layout: MutableList<CommandButton>
        ): ListenableFuture<SessionResult> {
            Timber.d("MediaControllerListener onSetCustomLayout")
            return super.onSetCustomLayout(controller, layout)
        }

        override fun onAvailableSessionCommandsChanged(
            controller: MediaController,
            commands: SessionCommands
        ) {
            Timber.d("MediaControllerListener onAvailableSessionCommandsChanged")
            super.onAvailableSessionCommandsChanged(controller, commands)
        }

        override fun onCustomCommand(
            controller: MediaController,
            command: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            Timber.d("MediaControllerListener onCustomCommand")
            return super.onCustomCommand(controller, command, args)
        }

        override fun onExtrasChanged(controller: MediaController, extras: Bundle) {
            Timber.d("MediaControllerListener onExtrasChanged")
            super.onExtrasChanged(controller, extras)
        }
    }
}