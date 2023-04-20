@file:UnstableApi
@file:OptIn(ExperimentalComposeUiApi::class)

package io.obolonsky.player

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle.Event.*
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.*
import androidx.media3.ui.PlayerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.codeboy.pager2_transformers.Pager2_ZoomOutTransformer
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import io.obolonsky.core.di.actions.StartDownloadServiceAction
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.player.databinding.FragmentPlayerBinding
import io.obolonsky.player.databinding.FragmentPlayerNavigationBinding
import io.obolonsky.player.di.PlayerComponent
import io.obolonsky.player.player.PodcasterPlaybackService
import io.obolonsky.player.player.PodcasterPlaybackService.Companion.getPlayerComponent
import io.obolonsky.player.ui.ImagesAdapter
import io.obolonsky.player.ui.compose.PlayerTheme
import io.obolonsky.utils.get
import timber.log.Timber
import java.util.concurrent.ExecutionException
import javax.inject.Inject
import javax.inject.Provider
import io.obolonsky.coreui.R as CoreUiR

class PlayerFragment : Fragment() {

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

    private val playerListener by lazy {
        object : Player.Listener {

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                onMediaMetadata(mediaMetadata)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {

                PlayerScreen()
            }
        }
    }

    private val mediaImagesAdapter by lazy { ImagesAdapter() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    // TODO: move to composable
    fun initViews(savedInstanceState: Bundle?) {
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

    class MediaControllerListener : MediaController.Listener {

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

@Composable
fun PlayerScreen() = PlayerTheme {
    val context = LocalContext.current.applicationContext
    val lifecycleOwner = LocalLifecycleOwner.current

    var player: Player? by remember {
        mutableStateOf(null)
    }

    var controllerFuture: ListenableFuture<MediaController>? = remember { null }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                ON_START -> {
                    controllerFuture = MediaController.Builder(
                        context,
                        SessionToken(context, ComponentName(context, PodcasterPlaybackService::class.java))
                    )
                        .setListener(PlayerFragment.MediaControllerListener())
                        .buildAsync()

                    controllerFuture?.addListener({
                        try {
                            player = controllerFuture?.get()
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
                ON_STOP -> {
                    player = null
                    controllerFuture?.let(MediaController::releaseFuture)
                }
                else -> { }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ComposePlayer(player)
}

// TODO: set player null on ON_STOP event
@SuppressLint("InflateParams")
@Composable
fun ComposePlayer(
    player: Player?,
) = Box(modifier = Modifier.fillMaxSize()) {
    val context = LocalContext.current

    AndroidView(
        factory = {
            val playerView = LayoutInflater
                .from(context)
                .inflate(R.layout.fragment_player, null) as PlayerView

            playerView.apply {
                showController()
            }
        },
        update = { playerView ->
            playerView.player = player
        },
        onReset = { },
        onRelease = { playerView ->
            playerView.player = null
        }
    )
}