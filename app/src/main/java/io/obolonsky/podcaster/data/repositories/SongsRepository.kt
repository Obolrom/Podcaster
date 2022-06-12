package io.obolonsky.podcaster.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.haroldadmin.cnradapter.NetworkResponse
import io.obolonsky.podcaster.api.BookApi
import io.obolonsky.podcaster.data.misc.BookMapper
import io.obolonsky.podcaster.data.misc.BookPagingMapper
import io.obolonsky.podcaster.data.responses.BookProgressRequest
import io.obolonsky.podcaster.data.room.PodcasterDatabase
import io.obolonsky.podcaster.data.room.StatefulData
import io.obolonsky.podcaster.data.room.daos.SongDao
import io.obolonsky.podcaster.data.room.entities.Book
import io.obolonsky.podcaster.data.room.entities.Chapter
import io.obolonsky.podcaster.di.modules.CoroutineSchedulers
import io.obolonsky.podcaster.paging.BookPagingSource
import io.obolonsky.podcaster.paging.BookSearchPagingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongsRepository @Inject constructor(
    private val bookApi: BookApi,
    private val database: PodcasterDatabase,
    private val songsDao: SongDao,
    private val dispatchers: CoroutineSchedulers,
) {

    val chapters = mutableListOf<Chapter>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
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

            when (val shit = bookApi.getUserBookLibrary()) {
                is NetworkResponse.Success -> {
                    Timber.d("asdgnnflskjfs lib is working: ${shit.body}")
                }
                is NetworkResponse.Error -> {
                    Timber.d("asdgnnflskjfs lib error: ${shit.error}")
                }
            }
        }
    }

    suspend fun saveAuditionProgress(
        bookId: String,
        chapterId: String,
        progress: Long,
    ) = withContext(dispatchers.io) {
        bookApi.postProgress(
            BookProgressRequest(
                bookId /*"3fdd18e1-af5f-44a2-8863-5c283563c0ac"*/,
                "5AFAEC8E-7C32-4008-9762-48C04D73B8C0",
                chapterId /*"2fe26e2e-bbc0-4443-bfa8-e2c496077f05"*/,
                progress /*20003*/,
            )
        )
    }

    fun loadBooks(pagingConfig: PagingConfig): Flow<PagingData<Book>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = {
                BookPagingSource(bookApi)
            }
        ).flow
    }

    fun search(query: String, pagingConfig: PagingConfig): Flow<PagingData<Book>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = {
                BookSearchPagingSource(
                    bookApi,
                    query,
                )
            }
        ).flow
    }

    suspend fun search(query: String) = withContext(dispatchers.io) {
        when (val response = bookApi.getSearchRange(query, 0, 30)) {
            is NetworkResponse.Success -> {
                withContext(dispatchers.computation) {
                    response.body.map { BookPagingMapper.map(it) }
                }
            }

            is NetworkResponse.Error -> {
                Timber.d("fuckingSearch error")
                emptyList()
            }
        }
    }

    suspend fun loadBook(id: String): StatefulData<Book> {
        val response = bookApi.getBookDetails(
            bookId = id,
        )
        return when (response) {
            is NetworkResponse.Success -> {
                val data = BookMapper.map(response.body)
                chapters.clear()
                chapters.addAll(data.chapters)
                StatefulData.Success(data)
            }

            is NetworkResponse.Error -> {
                StatefulData.Error(response.error ?: Exception("Unknown error"))
            }
        }
    }
}