package io.obolonsky.shazam.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.core.di.actions.CreatePlayerScreenAction
import io.obolonsky.core.di.actions.NavigateToDownloadsAction
import io.obolonsky.core.di.actions.StopPlayerService
import io.obolonsky.core.di.common.AudioSource
import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.core.di.toaster
import io.obolonsky.shazam.R
import io.obolonsky.shazam.databinding.ActivityShazamBinding
import io.obolonsky.shazam.redux.ShazamAudioRecordingSideEffects
import io.obolonsky.shazam.redux.ShazamAudioRecordingState
import io.obolonsky.shazam.ui.compose.theme.ShazamTheme
import io.obolonsky.shazam.viewmodels.ComponentViewModel
import io.obolonsky.shazam.viewmodels.ShazamViewModel
import io.obolonsky.utils.get
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider
import io.obolonsky.core.R as CoreR
import io.obolonsky.coreui.R as CoreUiR

class ShazamActivity : AppCompatActivity() {

    @Inject
    internal lateinit var createPlayerScreenAction: Provider<CreatePlayerScreenAction>

    @Inject
    internal lateinit var stopPlayerServiceAction: Provider<StopPlayerService>

    @Inject
    internal lateinit var navigateToDownloadsAction: NavigateToDownloadsAction

    private val componentViewModel by viewModels<ComponentViewModel>()

    private val shazamViewModel: ShazamViewModel by lazyViewModel {
        componentViewModel.shazamComponent
            .shazamViewModel()
            .create(
                savedStateHandle = it,
                outputFilepath = File(filesDir, RECORDED_AUDIO_FILENAME).absolutePath,
                outputDir = filesDir.absolutePath,
                recordDurationMs = TimeUnit.SECONDS.toMillis(7),
            )
    }

    private val binding: ActivityShazamBinding by viewBinding()

    private val toaster by toaster()

    private val recordPermission = MediaRecorderPermission(
        activityResultRegistry = activityResultRegistry,
        onPermissionGranted = ::onRecordPermissionGranted,
    )

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.handleIntent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        componentViewModel.shazamComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shazam)

        intent?.handleIntent()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStackImmediate()
                    stopPlayerServiceAction.get {
                        stop(this@ShazamActivity)
                    }
                    AudioSource.clear()
                } else {
                    finish()
                }
            }
        })

        binding.composeView.setContent {
            val state by shazamViewModel.collectAsState()
            shazamViewModel.collectSideEffect(sideEffect = ::onSideEffect)

            Screen(
                state = state,
                onRequestRecordPermission = recordPermission::requestRecordPermission,
            )
        }
    }

    private fun Track.metadata(): MediaMetadata {
        return MediaMetadata.Builder()
            .setArtworkUri(imageUrls.firstOrNull()?.toUri())
            .setDisplayTitle(title)
            .setTitle(title)
            .apply {
                setExtras(
                    bundleOf(
                        "mediaUrl" to audioUri,
                        "shazam_images" to imageUrls.take(2)
                    )
                )
            }
            .setArtist(subtitle)
            .build()
    }

    private fun showPlayer() {
        createPlayerScreenAction.get {
            supportFragmentManager.commit {
                addToBackStack(null)
                add(R.id.container, showPlayer())
            }
        }
    }

    private fun Intent.handleIntent() {
        when (action) {
            // When the BII is matched, Intent.Action_VIEW will be used
            Intent.ACTION_VIEW -> {
                toaster.showToast(this@ShazamActivity, "handle intent")
            }

            // Otherwise start the app as you would normally do.
            else -> {
//                toaster.showToast(this@ShazamActivity, "intent not handled")
            }
        }
    }

    private fun onSideEffect(effect: ShazamAudioRecordingSideEffects): Unit = when (effect) {
        is ShazamAudioRecordingSideEffects.ShazamDetectedSideEffect -> {
            onTrackDetected(effect.detectedTrack)
        }
    }

    private fun onTrackDetected(track: Track) {
        track.audioUri?.let { audioUri ->
            AudioSource.addTrack(track)
            AudioSource.addMediaItem(
                MediaItem.Builder()
                    .setRequestMetadata(
                        MediaItem.RequestMetadata.Builder()
                            .setMediaUri(audioUri.toUri())
                            .build()
                    )
                    .setUri(audioUri)
                    .setMediaMetadata(track.metadata())
                    .build()
            )
        }
        track.relatedTracks.onEach { relatedTrack ->
            AudioSource.addTrack(relatedTrack)
            relatedTrack.audioUri?.let { audioUri ->
                AudioSource.addMediaItem(
                    MediaItem.Builder()
                        .setRequestMetadata(
                            MediaItem.RequestMetadata.Builder()
                                .setMediaUri(audioUri.toUri())
                                .build()
                        )
                        .setUri(audioUri)
                        .setMediaMetadata(relatedTrack.metadata())
                        .build()
                )
            }
        }
        showPlayer()
    }

    private fun onRecordPermissionGranted() {
        shazamViewModel.record()
    }

    private companion object {
        const val RECORDED_AUDIO_FILENAME = "fileToDetect.mp3"
    }
}

@Composable
fun Screen(
    state: ShazamAudioRecordingState,
    onRequestRecordPermission: () -> Unit,
) = ShazamTheme {
    Box(
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Blue.copy(alpha = 0.45f),
                        Color.Blue.copy(alpha = 0.55f),
                        Color.Blue.copy(alpha = 0.75f),
                    ),
                )
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            TapToShazamMessage()

            WavesAnimation(
                state = state,
                onRequestRecordPermission = onRequestRecordPermission,
            )
        }
    }
}

@Composable
fun TapToShazamMessage(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = stringResource(CoreR.string.shazam_tap_to_shazam),
        style = MaterialTheme.typography.h5,
        fontWeight = FontWeight.W500,
        color = Color.White,
    )
}

@Composable
fun WavesAnimation(
    state: ShazamAudioRecordingState,
    onRequestRecordPermission: () -> Unit,
) {
    var wavesAlpha by remember { mutableStateOf(0f) }

    val waves = listOf(
        remember { Animatable(0f) },
        remember { Animatable(0f) },
        remember { Animatable(0f) },
        remember { Animatable(0f) },
    )

    val animationSpec = infiniteRepeatable<Float>(
        animation = tween(4000, easing = FastOutLinearInEasing),
        repeatMode = RepeatMode.Restart,
    )

    waves.forEachIndexed { index, animatable ->
        LaunchedEffect(animatable) {
            delay(index * 1000L)
            animatable.animateTo(
                targetValue = 1f, animationSpec = animationSpec
            )
        }
    }

    val dys = waves.map { it.value }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Center
    ) {
        dys.forEach { dy ->
            Box(
                Modifier
                    .size(136.dp)
                    .align(Center)
                    .graphicsLayer {
                        scaleX = dy * 2 + 1
                        scaleY = dy * 2 + 1
                        alpha = 1 - dy
                    }
                    .alpha(wavesAlpha),
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(color = Color.White, shape = CircleShape)
                )
            }
        }

        ShazamButton(
            state = state,
            onRequestRecordPermission = onRequestRecordPermission,
            onRecordingStateChange = { isRecording ->
                wavesAlpha = if (isRecording) 0.5f else 0.0f
            }
        )
    }
}

@Composable
fun ShazamButton(
    state: ShazamAudioRecordingState,
    onRequestRecordPermission: () -> Unit,
    onRecordingStateChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isButtonScaled = state.isRecordingInProgress
    val scale = remember { Animatable(initialValue = 1f) }

    onRecordingStateChange(isButtonScaled)

    LaunchedEffect(isButtonScaled) {
        if (isButtonScaled) {
            repeat(7) {
                scale.animateTo(
                    targetValue = 1.2f,
                    animationSpec = tween(durationMillis = 500)
                )
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 500)
                )
            }
        } else {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500)
            )
        }
    }

    Surface(
        color = colorResource(id = CoreUiR.color.blue),
        shape = CircleShape,
        modifier = modifier
            .scale(scale.value)
            .size(152.dp)
            .clip(CircleShape)
            .clickable {
                onRequestRecordPermission()
            },
    ) {
        Icon(
            modifier = Modifier.padding(16.dp),
            painter = painterResource(id = CoreUiR.drawable.shazam),
            tint = Color.White,
            contentDescription = null,
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun SecondPreview() {
    val state = ShazamAudioRecordingState(
        detected = null,
        isRecordingInProgress = false,
    )

    Screen(
        state = state,
        onRequestRecordPermission = { },
    )
}