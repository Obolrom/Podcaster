package io.obolonsky.podcaster.ui

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts

class DefaultMediaRecorder(
    activityResultRegistry: ActivityResultRegistry,
    private val onAudioUriCallback: (Uri) -> Unit,
) {

    private val recorderContent = activityResultRegistry.register(
        KEY, ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        activityResult?.data?.data?.let(onAudioUriCallback)
    }

    fun recordAudio() {
        val recorderIntent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        // TODO: investigate
//        RecognizerIntent
//        recorderIntent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/wav")
//        recorderIntent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/mp3")
        recorderIntent.putExtra("android.speech.extra.GET_AUDIO", true)
        recorderContent.launch(recorderIntent)
    }

    private companion object {
        const val KEY = "DefaultMediaRecorder"
    }
}