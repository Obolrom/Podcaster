package io.obolonsky.shazam.data.usecases

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.shazam.di.ScopedShazamRepo
import java.io.File
import javax.inject.Inject

class AudioDetectionUseCase @Inject constructor(
    private val shazamRepository: ScopedShazamRepo,
) {

    suspend operator fun invoke(audioFile: File): Reaction<ShazamDetect, Error> {
        return shazamRepository.audioDetect(audioFile)
    }
}