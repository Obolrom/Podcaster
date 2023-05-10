package io.obolonsky.shazam.recorder

import android.media.MediaRecorder
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.IOException

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