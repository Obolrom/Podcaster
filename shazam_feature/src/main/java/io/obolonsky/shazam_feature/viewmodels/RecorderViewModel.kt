package io.obolonsky.shazam_feature.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.shazam_feature.recorder.ShazamMediaRecorderImpl
import kotlinx.coroutines.flow.*
import java.io.File

class RecorderViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    @Assisted outputFile: File,
    @Assisted recordDurationMs: Long,
) : ViewModel() {

    private val _audioRecordingComplete by lazy { MutableSharedFlow<Unit>() }
    val audioRecordingComplete: SharedFlow<Unit> get() = _audioRecordingComplete.asSharedFlow()

    private val audioRecorder by lazy {
        ShazamMediaRecorderImpl(
            outputFile = outputFile,
            recordDurationMs = recordDurationMs,
            coroutineScope = viewModelScope,
            onMediaRecordedFlow = _audioRecordingComplete,
        )
    }

    fun record() {
        audioRecorder.startRecording()
    }

    @AssistedFactory
    interface Factory {

        fun create(
            savedStateHandle: SavedStateHandle,
            outputFile: File,
            recordDurationMs: Long,
        ): RecorderViewModel
    }
}