package io.obolonsky.podcaster.data.repositories

import androidx.room.withTransaction
import io.obolonsky.podcaster.api.TestMusicLibraryApi
import io.obolonsky.podcaster.data.mappers.*
import io.obolonsky.podcaster.data.room.PodcasterDatabase
import io.obolonsky.podcaster.data.room.daos.SongDao
import io.obolonsky.podcaster.data.room.load
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongsRepository @Inject constructor(
    private val musicLibraryApi: TestMusicLibraryApi,
    private val database: PodcasterDatabase,
    private val songsDao: SongDao,
) {

    fun getMusicItems(shouldFetch: Boolean = true) = load(
        query = { songsDao.getSongs() },
        fetch = {
            musicLibraryApi.getMusic().mediaItems
        },
        saveFetchResult = { musicItems ->
            database.withTransaction {
                songsDao.deleteSongs()
                songsDao.insertSongs(musicItems.mapList(MusicItemToSongMapper))
            }
        },
        shouldFetch = { shouldFetch }
    )
}