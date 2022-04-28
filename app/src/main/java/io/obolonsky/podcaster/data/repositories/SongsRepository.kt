package io.obolonsky.podcaster.data.repositories

import io.obolonsky.podcaster.api.BookApi
import io.obolonsky.podcaster.data.misc.BookMapper
import io.obolonsky.podcaster.data.misc.handle
import io.obolonsky.podcaster.data.responses.BookProgressRequest
import io.obolonsky.podcaster.data.responses.MusicItem
import io.obolonsky.podcaster.data.room.PodcasterDatabase
import io.obolonsky.podcaster.data.room.daos.SongDao
import io.obolonsky.podcaster.data.room.entities.Chapter
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongsRepository @Inject constructor(
    private val bookApi: BookApi,
    private val database: PodcasterDatabase,
    private val songsDao: SongDao,
) {

    val chapters = mutableListOf<Chapter>()

    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.d("okHttp error: ${throwable.message}")
    }

    init {
        CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
            val response = bookApi.getBookDetails(
                bookId = "4c652d97-ab4a-4897-85f1-1257a2e59200",
                personId = "5AFAEC8E-7C32-4008-9762-48C04D73B8C0",
            )
            chapters.addAll(BookMapper.map(response.handle()).chapters)

            val post = async(exceptionHandler) {
                bookApi.postProgress(
                    BookProgressRequest(
                        "3fdd18e1-af5f-44a2-8863-5c283563c0ac",
                        "5AFAEC8E-7C32-4008-9762-48C04D73B8C0",
                        "2fe26e2e-bbc0-4443-bfa8-e2c496077f05",
                        20003,
                    )
                )
            }
            post.await()

            bookApi.getBookRange(0, 5)
        }
    }
}