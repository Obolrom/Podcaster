@file:UnstableApi
@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)

package io.obolonsky.player

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle.Event.*
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.*
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.github.fengdai.compose.media.TimeBar
import com.github.fengdai.compose.media.TimeBarProgress
import com.github.fengdai.compose.media.TimeBarScrubber
import com.google.android.material.math.MathUtils
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import io.obolonsky.core.di.actions.StartDownloadServiceAction
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.player.di.PlayerComponent
import io.obolonsky.player.player.PodcasterPlaybackService
import io.obolonsky.player.player.PodcasterPlaybackService.Companion.getPlayerComponent
import io.obolonsky.player.ui.compose.PlayerTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import timber.log.Timber
import java.util.concurrent.ExecutionException
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.roundToInt
import io.obolonsky.coreui.R as CoreUiR

class PlayerFragment : Fragment() {

    @Inject
    internal lateinit var startDownloadServiceAction: Provider<StartDownloadServiceAction>

    @Suppress("unused")
    private val downloadViewModel by lazyViewModel {
        getComponent()
            .downloadViewModelFactory()
            .create(it)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getComponent().inject(this)
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

    private fun getComponent(): PlayerComponent {
        return getPlayerComponent(
            applicationProvider = (activity?.applicationContext as App).getAppComponent()
        )
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
    var bufferPosition by rememberSaveable {
        mutableStateOf(0L)
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
                bufferPosition = player.bufferedPosition
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
                player?.bufferedPosition?.let { bufferedPercentage -> bufferPosition = bufferedPercentage }
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
        bufferPercentage = { bufferPosition },
        onSeekChanged = { player?.seekTo(it) },
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
    bufferPercentage: () -> Long,
    onSeekChanged: (timeMs: Long) -> Unit,
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
    bufferPercentage: () -> Long,
    onSeekChanged: (Long) -> Unit,
    modifier: Modifier = Modifier,
) = Box(modifier = modifier.background(Color.Black)) {

    var mediaControlsVerticalOffset by remember {
        mutableStateOf(0.dp)
    }

    MyBottomSheet(
        onSheetOffsetChanged = { offset ->
            mediaControlsVerticalOffset = offset
        },
        background = {
            PlayerBackground(
                modifier = Modifier.fillMaxWidth(),
                metadata = metadata,
                overlay = {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .height(152.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Black, Color.Transparent)
                                )
                            ),
                    ) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                modifier = Modifier,
                                text = metadata().title.toString(),
                                color = Color.White,
                                style = Typography().subtitle1,
                                fontWeight = FontWeight.W500,
                            )
                            Text(
                                modifier = Modifier.padding(top = 4.dp),
                                text = metadata().artist.toString(),
                                color = Color.White,
                                style = Typography().subtitle2,
                            )
                        }
                    }
                }
            ) {
                Column(modifier = Modifier) {
                    MediaControls(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = mediaControlsVerticalOffset),
                        isPlaying = isPlaying,
                        onPlayPause = onPlayPause,
                        onPrevious = onPrevious,
                        onForward = onForward,
                    )
                }
            }
        },
        header = {
            TimeBar(
                durationMs = totalDuration(),
                positionMs = currentTime(),
                bufferedPositionMs = bufferPercentage(),
                modifier = Modifier
                    .systemGestureExclusion()
                    .fillMaxWidth()
                    .height(36.dp),
                contentPadding = PaddingValues(top = 28.dp),
                progress = { _, scrubbed, buffered ->
                    TimeBarProgress(
                        played = scrubbed,
                        buffered = buffered,
                        playedColor = Color.Blue,
                        bufferedColor = Color.LightGray,
                    )
                },
                scrubber = { enabled, scrubbing ->
                    TimeBarScrubber(
                        enabled = enabled,
                        scrubbing = scrubbing,
                        color = Color.Transparent,
                    )
                },
                onScrubStop = onSeekChanged,
            )
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(0.74f),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Yellow),
            ) {
                items((0..50).toList()) { item ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .padding(4.dp)
                            .background(Color.Gray),
                        text = item.toString(),
                    )
                }
            }
        }
    }
}

// https://proandroiddev.com/how-to-master-swipeable-and-nestedscroll-modifiers-in-compose-bb0635d6a760
@Composable
fun MyBottomSheet(
    onSheetOffsetChanged: (Dp) -> Unit,
    background: @Composable () -> Unit,
    header: @Composable () -> Unit,
    body: @Composable () -> Unit,
) {
    val swipeableState = rememberSwipeableState(initialValue = States.COLLAPSED)
    val scrollState = rememberScrollState()

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
    ) {
        val constraintsScope = this
        val maxOffsetHeight = with(LocalDensity.current) {
            (constraintsScope.maxHeight * 0.8f).toPx()
        }
        val minOffsetHeight = with(LocalDensity.current) {
            (constraintsScope.maxHeight * 0.25f).toPx()
        }
        val connection = remember {
            object : NestedScrollConnection {

                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    val delta = available.y
                    return if (delta < 0) {
                        swipeableState.performDrag(delta).toOffset()
                    } else {
                        Offset.Zero
                    }
                }

                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    val delta = available.y
                    return swipeableState.performDrag(delta).toOffset()
                }

                override suspend fun onPreFling(available: Velocity): Velocity {
                    return if (available.y < 0 && scrollState.value == 0) {
                        swipeableState.performFling(available.y)
                        available
                    } else {
                        Velocity.Zero
                    }
                }

                override suspend fun onPostFling(
                    consumed: Velocity,
                    available: Velocity
                ): Velocity {
                    swipeableState.performFling(velocity = available.y)
                    return super.onPostFling(consumed, available)
                }

                private fun Float.toOffset() = Offset(0f, this)
            }
        }

        if (swipeableState.progress.to == States.COLLAPSED) {
            onSheetOffsetChanged(MathUtils.lerp((-440).dp.value, 0.dp.value, swipeableState.progress.fraction).dp)
        } else {
            onSheetOffsetChanged(MathUtils.lerp(0.dp.value, (-440).dp.value, swipeableState.progress.fraction).dp)
        }

        background()

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = 0,
                        y = swipeableState.offset.value.roundToInt(),
                    )
                }
                .swipeable(
                    state = swipeableState,
                    orientation = Orientation.Vertical,
                    anchors = mapOf(
                        minOffsetHeight to States.EXPANDED,
                        maxOffsetHeight to States.COLLAPSED,
                    ),
                )
                .nestedScroll(connection),
        ) {
            Column(Modifier.fillMaxHeight()) {
                header()
                Box(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    body()
                }
            }
        }
    }
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
            colorFilter = ColorFilter.tint(Color.Blue),
            contentDescription = null,
        )
    }

    Box(
        modifier = Modifier
            .size(72.dp)
            .background(Color.Blue, CircleShape)
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
            colorFilter = ColorFilter.tint(Color.White),
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
            colorFilter = ColorFilter.tint(Color.Blue),
            contentDescription = null,
        )
    }
}

@Composable
fun PlayerBackground(
    metadata: () -> MediaMetadata,
    modifier: Modifier = Modifier,
    overlay: @Composable BoxScope.() -> Unit,
    content: @Composable () -> Unit,
) = Column(modifier = modifier) {
    val imageUrl = remember(metadata()) {
        mutableStateOf(
            metadata().extras
                ?.getStringArrayList("shazam_images")
                ?.getOrNull(1)
        )
    }

    Box(
        modifier = Modifier.weight(7f),
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize(),
            model = imageUrl.value,
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )
        overlay()
        Spacer(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(152.dp)
                .background(brush = Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))
        )
    }

    Box(
        modifier = Modifier.weight(3f),
    ) {
        content()
    }
}

enum class States {
    EXPANDED,
    COLLAPSED
}