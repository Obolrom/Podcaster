package io.obolonsky.podcaster.data.repositories

import androidx.room.withTransaction
import io.obolonsky.podcaster.api.TestMusicLibraryApi
import io.obolonsky.podcaster.data.responses.MusicItem
import io.obolonsky.podcaster.data.room.PodcasterDatabase
import io.obolonsky.podcaster.data.room.daos.SongDao
import io.obolonsky.podcaster.data.room.entities.Song
import io.obolonsky.podcaster.data.room.load
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongsRepository @Inject constructor(
    private val musicLibraryApi: TestMusicLibraryApi,
    private val database: PodcasterDatabase,
    private val songsDao: SongDao,
) {

    suspend fun getItems() = /*musicLibraryApi.getMusic().mediaItems*/listOf(
        MusicItem("Otherside", 1488L, "https://github.com/Obolrom/MusicLibrary/blob/master/rhcp_californication/red-hot-chili-peppers-otherside.mp3?raw=true"),
        MusicItem("Californication", 1488L, "https://github.com/Obolrom/MusicLibrary/blob/master/rhcp_californication/red-hot-chili-peppers-californication.mp3?raw=true"),
        MusicItem("Can't stop", 1488L, "https://github.com/Obolrom/MusicLibrary/blob/master/rhcp_californication/red-hot-chili-peppers-cant-stop.mp3?raw=true"),
        MusicItem("Around the world", 1488L, "https://github.com/Obolrom/MusicLibrary/blob/master/rhcp_californication/red-hot-chili-peppers-around-the-world.mp3?raw=true"),
    )

    suspend fun getSongs() = getItems().map {
        Song(
            id = it.id,
            title = it.title ?: "no Info",
            mediaUrl = it.mediaUrl,
            isFavorite = false,
            mediaId = "RHCP"
        )
    }

    fun getMusicItems() = load(
        query = { songsDao.getSongs() },
        fetch = {
            musicLibraryApi.getMusic().mediaItems
        },
        saveFetchResult = { songs ->
            database.withTransaction {
                songsDao.deleteSongs()
                songsDao.insertSongs(songs.map {
                    Song(
                        id = it.id,
                        title = it.title,
                        mediaUrl = it.mediaUrl,
                    )
                })
            }
        }
    )
}