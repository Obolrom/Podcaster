package io.obolonsky.shazam.recorder

import android.media.MediaRecorder
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.File
import java.io.IOException

class ShazamMediaRecorderImpl(
    private val outputFile: File,
    private val recordDurationMs: Long,
    private val coroutineScope: CoroutineScope,
    private val onMediaRecordedFlow: MutableSharedFlow<Unit>,
) : AudioRecorder {

    private var recorder: MediaRecorder? = null

    override fun start() {
        startRecording()
    }

    override fun stop() {
        coroutineScope.launch {
            stopRecording()
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    fun startRecording() {
        coroutineScope.launch {
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
                setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
                setOutputFile(outputFile.absolutePath)

                try {
                    prepare()
                } catch (e: IOException) {
                    Timber.e(e)
                }

                start()
            }
        }

        coroutineScope.launch {
            delay(recordDurationMs)
            stopRecording()
        }
    }

    private suspend fun stopRecording() = coroutineScope {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        onMediaRecordedFlow.emit(Unit)
    }
}

class ShazamMediaRecorder(
    private val outputFile: File,
    private val recordDurationMs: Long,
) {

    private var recorder: MediaRecorder? = null

    suspend fun record() = coroutineScope {
        launch {
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
                setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
                setOutputFile(outputFile.absolutePath)

                try {
                    prepare()
                } catch (e: IOException) {
                    Timber.e(e)
                }

                start()
            }
        }

        launch {
            delay(recordDurationMs)
            stopRecording()
        }
    }

    private suspend fun stopRecording() = coroutineScope {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }
}