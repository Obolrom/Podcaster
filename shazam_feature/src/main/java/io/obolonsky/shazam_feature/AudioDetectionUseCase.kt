package io.obolonsky.shazam_feature

import io.obolonsky.core.di.data.ShazamDetect
import java.io.File
import javax.inject.Inject

class AudioDetectionUseCase @Inject constructor(
    private val shazamRepository: ShazamRepository,
) {

    suspend operator fun invoke(audioFile: File): ShazamDetect? {
        return shazamRepository.audioDetect(audioFile)
    }
}