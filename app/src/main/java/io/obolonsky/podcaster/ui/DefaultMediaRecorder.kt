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
        recorderContent.launch(recorderIntent)
    }

    private companion object {
        const val KEY = "DefaultMediaRecorder"
    }
}