package io.obolonsky.shazam.data.usecases

import io.obolonsky.core.di.data.Track
import io.obolonsky.shazam.di.ScopedShazamRepo
import javax.inject.Inject

class DeleteRecentTrackUseCase @Inject constructor(
    private val shazamRepository: ScopedShazamRepo,
) {

    suspend operator fun invoke(track: Track) {
        return shazamRepository.deleteRecentTrackTrack(track)
    }
}