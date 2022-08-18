package io.obolonsky.shazam_feature.ui

import android.Manifest
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts

class MediaRecorderPermission(
    activityResultRegistry: ActivityResultRegistry,
    private val onPermissionGranted: () -> Unit,
) {

    private val recordPermission = activityResultRegistry.register(
        KEY, ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        }
    }

    fun requestRecordPermission() {
        recordPermission.launch(Manifest.permission.RECORD_AUDIO)
    }

    private companion object {
        const val KEY = "MediaRecorderPermission"
    }
}