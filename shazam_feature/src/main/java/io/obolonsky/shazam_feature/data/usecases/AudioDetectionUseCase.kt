package io.obolonsky.shazam_feature.data.usecases

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.shazam_feature.di.ScopedShazamRepo
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class AudioDetectionUseCase @Inject constructor(
    private val shazamRepository: ScopedShazamRepo,
) {

    init {
        Timber.d("ShazamRepoPro useCase: ${System.identityHashCode(shazamRepository)}")
    }

    suspend operator fun invoke(audioFile: File): Reaction<ShazamDetect, Error> {
        return shazamRepository.audioDetect(audioFile)
    }
}