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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle.Event.*
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.*
import androidx.media3.ui.PlayerView
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.compose.AsyncImage
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
import io.obolonsky.player.ui.compose.PlayerTheme
import io.obolonsky.utils.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import timber.log.Timber
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    // TODO: move to composable
    fun initViews(savedInstanceState: Bundle?) {
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
    val isAudioPlaying = rememberSaveable {
        mutableStateOf(false)
    }
    var metadata by remember {
        mutableStateOf(MediaMetadata.EMPTY)
    }
    var totalDuration by rememberSaveable {
        mutableStateOf(0L)
    }
    var currentTime by rememberSaveable {
        mutableStateOf(0L)
    }
    var bufferPercentage by rememberSaveable {
        mutableStateOf(0)
    }

    var controllerFuture: ListenableFuture<MediaController>? = remember { null }

    DisposableEffect(lifecycleOwner) {
        val playerListener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                isAudioPlaying.value = isPlaying
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                metadata = mediaMetadata
            }

            override fun onEvents(player: Player, events: Player.Events) {
                totalDuration = player.duration.coerceAtLeast(0L)
                currentTime = player.currentPosition.coerceAtLeast(0L)
                bufferPercentage = player.bufferedPercentage
            }
        }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                ON_START -> {
                    val sessionToken = SessionToken(
                        context,
                        ComponentName(context, PodcasterPlaybackService::class.java)
                    )
                    controllerFuture = MediaController.Builder(context, sessionToken)
                        .setListener(PlayerFragment.MediaControllerListener())
                        .buildAsync()

                    controllerFuture?.addListener({
                        try {
                            player = controllerFuture?.get()
                            player?.addListener(playerListener)
                            player?.mediaMetadata?.let { metadata = it }
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
            player?.removeListener(playerListener)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (isAudioPlaying.value) {
        LaunchedEffect(lifecycleOwner) {
            while (isActive) {
                player?.currentPosition?.let { currentPosition -> currentTime = currentPosition }
                player?.bufferedPercentage?.let { bufferedPercentage -> bufferPercentage = bufferedPercentage }
                delay(500L)
            }
        }
    }

    ComposePlayer(
        player = player,
        isPlaying = { isAudioPlaying.value },
        onPlayPause = {
            if (player?.isPlaying == true) player?.pause()
            else player?.play()
        },
        metadata = { metadata },
        onPrevious = { player?.seekToPrevious() },
        onForward = { player?.seekToNext() },
        totalDuration = { totalDuration },
        currentTime = { currentTime },
        bufferPercentage = { bufferPercentage },
        onSeekChanged = { player?.seekTo(it.toLong()) },
    )
}

// TODO: set player null on ON_STOP event
@SuppressLint("InflateParams")
@Composable
fun ComposePlayer(
    player: Player?,
    isPlaying: () -> Boolean,
    onPlayPause: () -> Unit,
    metadata: () -> MediaMetadata,
    onPrevious: () -> Unit,
    onForward: () -> Unit,
    totalDuration: () -> Long,
    currentTime: () -> Long,
    bufferPercentage: () -> Int,
    onSeekChanged: (timeMs: Float) -> Unit,
) = Box(modifier = Modifier.fillMaxSize()) {
    val context = LocalContext.current

    AndroidView(
        factory = {
            val playerView = LayoutInflater
                .from(context)
                .inflate(R.layout.fragment_player, null) as PlayerView

            playerView.apply {
                useController = false
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

    PlayerControls(
        modifier = Modifier,
        isPlaying = isPlaying,
        onPlayPause = onPlayPause,
        metadata = metadata,
        onPrevious = onPrevious,
        onForward = onForward,
        totalDuration = totalDuration,
        currentTime = currentTime,
        bufferPercentage = bufferPercentage,
        onSeekChanged = onSeekChanged,
    )
}

@Composable
fun PlayerControls(
    isPlaying: () -> Boolean,
    onPlayPause: () -> Unit,
    metadata: () -> MediaMetadata,
    onPrevious: () -> Unit,
    onForward: () -> Unit,
    totalDuration: () -> Long,
    currentTime: () -> Long,
    bufferPercentage: () -> Int,
    onSeekChanged: (timeMs: Float) -> Unit,
    modifier: Modifier = Modifier,
) = Box(modifier = modifier.background(Color.Black)) {

    PlayerBackground(
        modifier = Modifier.fillMaxWidth(),
        metadata = metadata,
    ) {
        SeekBar(
            modifier = Modifier,
            totalDuration = totalDuration,
            currentTime = currentTime,
            bufferPercentage = bufferPercentage,
            onSeekChanged = onSeekChanged,
        )
    }

    Text(
        modifier = Modifier.align(Alignment.TopCenter),
        text = metadata().title.toString(),
        style = Typography().subtitle1,
    )

    MediaControls(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.Center),
        isPlaying = isPlaying,
        onPlayPause = onPlayPause,
        onPrevious = onPrevious,
        onForward = onForward,
    )
}

@Composable
fun SeekBar(
    totalDuration: () -> Long,
    currentTime: () -> Long,
    bufferPercentage: () -> Int,
    onSeekChanged: (timeMs: Float) -> Unit,
    modifier: Modifier = Modifier,
) = Box(modifier = modifier) {
    val duration = remember(totalDuration()) { totalDuration() }

    val videoTime = remember(currentTime()) { currentTime() }

    val buffer = remember(bufferPercentage()) { bufferPercentage() }

    Slider(
        modifier = Modifier
            .fillMaxWidth(),
        value = buffer.toFloat(),
        enabled = false,
        onValueChange = { },
        valueRange = 0f..100f,
        colors = SliderDefaults.colors(
            disabledThumbColor = Color.Transparent,
            disabledActiveTrackColor = Color.White,
        ),
    )

    Slider(
        modifier = Modifier.fillMaxWidth(),
        value = videoTime.toFloat(),
        onValueChange = onSeekChanged,
        valueRange = 0f..duration.toFloat(),
        colors = SliderDefaults.colors(
            thumbColor = Color.Blue,
            activeTrackColor = Color.Blue,
        ),
    )
}

@Composable
fun MediaControls(
    isPlaying: () -> Boolean,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onForward: () -> Unit,
    modifier: Modifier = Modifier,
) = Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(
        space = 24.dp,
        alignment = Alignment.CenterHorizontally
    ),
    verticalAlignment = Alignment.CenterVertically,
) {
    val isAudioPlaying = remember(isPlaying()) {
        isPlaying()
    }

    IconButton(
        onClick = onPrevious,
    ) {
        Image(
            modifier = Modifier.size(52.dp),
            contentScale = ContentScale.Crop,
            painter = painterResource(CoreUiR.drawable.ic_round_skip_previous),
            contentDescription = null,
        )
    }

    Box(
        modifier = Modifier
            .size(72.dp)
            .background(Color.White, CircleShape)
            .clip(CircleShape)
            .clickable { onPlayPause() },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            modifier = Modifier.size(52.dp),
            contentScale = ContentScale.Crop,
            painter =
            if (isAudioPlaying) painterResource(CoreUiR.drawable.ic_round_pause)
            else painterResource(CoreUiR.drawable.ic_round_play_arrow),
            contentDescription = null,
        )
    }

    IconButton(
        onClick = onForward,
    ) {
        Image(
            modifier = Modifier.size(52.dp),
            contentScale = ContentScale.Crop,
            painter = painterResource(CoreUiR.drawable.ic_round_skip_next),
            contentDescription = null,
        )
    }
}

@Composable
fun PlayerBackground(
    metadata: () -> MediaMetadata,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) = Column(modifier = modifier) {
    val imageUrl = remember(metadata()) {
        mutableStateOf(
            metadata().extras
                ?.getStringArrayList("shazam_images")
                ?.firstOrNull()
        )
    }

    Box(
        modifier = Modifier.weight(8f),
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize(),
            model = imageUrl.value,
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )
        Spacer(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(152.dp)
                .background(brush = Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))
        )
    }

    Box(
        modifier = Modifier.weight(2f),
    ) {
        content()
    }
}