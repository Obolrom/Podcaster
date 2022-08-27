package io.obolonsky.shazam_feature.data.usecases

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.repositories.ShazamRepo
import java.io.File
import javax.inject.Inject

class AudioDetectionUseCase @Inject constructor(
    private val shazamRepository: ShazamRepo,
) {

    suspend operator fun invoke(audioFile: File): Reaction<ShazamDetect, Error> {
        return shazamRepository.audioDetect(audioFile)
    }
}