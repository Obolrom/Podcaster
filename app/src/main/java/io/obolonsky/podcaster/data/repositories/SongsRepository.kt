package io.obolonsky.podcaster.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.haroldadmin.cnradapter.NetworkResponse
import io.obolonsky.podcaster.api.BookApi
import io.obolonsky.podcaster.data.misc.BookMapper
import io.obolonsky.podcaster.data.responses.BookProgressRequest
import io.obolonsky.podcaster.data.room.PodcasterDatabase
import io.obolonsky.podcaster.data.room.daos.SongDao
import io.obolonsky.podcaster.data.room.entities.Book
import io.obolonsky.podcaster.data.room.entities.Chapter
import io.obolonsky.podcaster.paging.BookPagingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongsRepository @Inject constructor(
    private val bookApi: BookApi,
    private val database: PodcasterDatabase,
    private val songsDao: SongDao,
) {

    val chapters = mutableListOf<Chapter>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val response = bookApi.getBookDetails(
                bookId = "4c652d97-ab4a-4897-85f1-1257a2e59200",
                personId = "5AFAEC8E-7C32-4008-9762-48C04D73B8C0",
            )
            when (response) {
                is NetworkResponse.Success -> {
                    chapters.addAll(BookMapper.map(response.body).chapters)
                }

                is NetworkResponse.Error -> {

                }
            }

            val progressResponse = bookApi.postProgress(
                    BookProgressRequest(
                        "3fdd18e1-af5f-44a2-8863-5c283563c0ac",
                        "5AFAEC8E-7C32-4008-9762-48C04D73B8C0",
                        "2fe26e2e-bbc0-4443-bfa8-e2c496077f05",
                        20003,
                    )
                )
            when (progressResponse) {
                is NetworkResponse.Success -> {
                    Timber.d("okHttp $progressResponse")
                }

                is NetworkResponse.Error -> {
                    Timber.d("okHttp $progressResponse")
                }
            }

            bookApi.getBookRange(0, 5)
        }
    }

    fun loadBooks(pagingConfig: PagingConfig): Flow<PagingData<Book>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = {
                BookPagingSource(bookApi)
            }
        ).flow
    }
}