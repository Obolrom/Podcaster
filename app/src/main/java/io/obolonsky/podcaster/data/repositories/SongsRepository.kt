package io.obolonsky.podcaster.data.repositories

import io.obolonsky.podcaster.api.TestMusicLibraryApi
import io.obolonsky.podcaster.data.responses.MusicItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongsRepository @Inject constructor(
    private val musicLibraryApi: TestMusicLibraryApi,
) {

    fun getMusicItems(): Flow<List<MusicItem>> =
        flow {
            val response = musicLibraryApi.getMusic()

            emit(response.mediaItems)
        }.flowOn(Dispatchers.IO)
}