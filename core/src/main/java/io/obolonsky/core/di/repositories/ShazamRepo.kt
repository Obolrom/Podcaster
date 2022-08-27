package io.obolonsky.core.di.repositories

import io.obolonsky.core.di.data.ShazamDetect
import java.io.File

interface ShazamRepo {

    suspend fun audioDetect(audioFile: File): ShazamDetect?
}