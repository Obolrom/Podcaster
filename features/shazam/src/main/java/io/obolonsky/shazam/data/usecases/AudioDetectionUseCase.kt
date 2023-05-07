package io.obolonsky.shazam.data.usecases

import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.shazam.di.ScopedShazamRepo
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class AudioDetectionUseCase @Inject constructor(
    private val shazamRepository: ScopedShazamRepo,
) {

    operator fun invoke(audioFile: File): Flow<Reaction<ShazamDetect>> {
        return shazamRepository.audioDetect(audioFile)
    }
}