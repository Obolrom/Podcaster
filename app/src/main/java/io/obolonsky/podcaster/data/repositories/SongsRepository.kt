package io.obolonsky.podcaster.data.repositories

import io.obolonsky.podcaster.api.BookApi
import io.obolonsky.podcaster.data.misc.BookMapper
import io.obolonsky.podcaster.data.responses.MusicItem
import io.obolonsky.podcaster.data.room.PodcasterDatabase
import io.obolonsky.podcaster.data.room.daos.SongDao
import io.obolonsky.podcaster.data.room.entities.Chapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongsRepository @Inject constructor(
    private val bookApi: BookApi,
    private val database: PodcasterDatabase,
    private val songsDao: SongDao,
) {

    suspend fun getItems() = /*musicLibraryApi.getMusic().mediaItems*/listOf(
        MusicItem("Otherside", 1488L, "https://github.com/Obolrom/MusicLibrary/blob/master/rhcp_californication/red-hot-chili-peppers-otherside.mp3?raw=true"),
        MusicItem("Californication", 1488L, "https://github.com/Obolrom/MusicLibrary/blob/master/rhcp_californication/red-hot-chili-peppers-californication.mp3?raw=true"),
        MusicItem("Can't stop", 1488L, "https://github.com/Obolrom/MusicLibrary/blob/master/rhcp_californication/red-hot-chili-peppers-cant-stop.mp3?raw=true"),
        MusicItem("Around the world", 1488L, "https://github.com/Obolrom/MusicLibrary/blob/master/rhcp_californication/red-hot-chili-peppers-around-the-world.mp3?raw=true"),
    )
    val chapters = mutableListOf<Chapter>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val response = bookApi.getBookDetails(
                bookId = "4c652d97-ab4a-4897-85f1-1257a2e59200",
                personId = "5AFAEC8E-7C32-4008-9762-48C04D73B8C0",
            )
            chapters
                .addAll(BookMapper.map(response).chapters)
        }
    }
}