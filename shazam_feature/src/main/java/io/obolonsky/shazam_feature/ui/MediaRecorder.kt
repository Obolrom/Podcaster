package io.obolonsky.shazam_feature.ui

import android.media.MediaRecorder
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.IOException

class MediaRecorder(
    private val outputFile: File,
    private val recordDurationMs: Long,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val onMediaRecorded: () -> Unit,
) {

    private var recorder: MediaRecorder? = null

    @Suppress("BlockingMethodInNonBlockingContext")
    fun startRecording() {
        lifecycleScope.launch {
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

        lifecycleScope.launch {
            delay(recordDurationMs)
            stopRecording()
        }
    }

    private fun stopRecording(){
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        onMediaRecorded()
    }
}