package io.obolonsky.downloads.usecases

import io.obolonsky.core.di.data.Track
import io.obolonsky.downloads.di.ScopedDownloadsRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTracksFlowUseCase @Inject constructor(
    private val downloadsRepo: ScopedDownloadsRepo,
) {

    operator fun invoke(): Flow<List<Track>> {
        return downloadsRepo.getTracksFlow()
    }
}