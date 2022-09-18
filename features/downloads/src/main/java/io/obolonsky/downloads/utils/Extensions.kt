package io.obolonsky.downloads.utils

import android.content.Context
import java.io.File

fun getDownloadDirectory(context: Context): File? {
    return context.getExternalFilesDir(null) ?: context.filesDir
}