package io.obolonsky.podcaster.data.repositories

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.haroldadmin.cnradapter.NetworkResponse
import io.obolonsky.core.di.scopes.ApplicationScope
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.api.BookApi
import io.obolonsky.podcaster.data.misc.BookMapper
import io.obolonsky.podcaster.data.misc.BookPagingMapper
import io.obolonsky.podcaster.data.misc.UserProfileMapper
import io.obolonsky.podcaster.data.responses.BookProgressRequest
import io.obolonsky.podcaster.data.room.PodcasterDatabase
import io.obolonsky.podcaster.data.room.StatefulData
import io.obolonsky.podcaster.data.room.daos.SongDao
import io.obolonsky.podcaster.data.room.entities.Book
import io.obolonsky.podcaster.data.room.entities.Chapter
import io.obolonsky.podcaster.di.modules.CoroutineSchedulers
import io.obolonsky.podcaster.paging.BookPagingSource
import io.obolonsky.podcaster.paging.BookSearchPagingSource
import io.obolonsky.shazam_feature.ShazamApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.io.BufferedReader
import java.io.File
import javax.inject.Inject

@ApplicationScope
class SongsRepository @Inject constructor(
    private val context: Context,
    private val bookApi: BookApi,
    private val shazamApi: ShazamApi,
    private val database: PodcasterDatabase,
    private val songsDao: SongDao,
    private val dispatchers: CoroutineSchedulers,
) {

    val chapters = mutableListOf<Chapter>()
        .apply {
            addAll(listOf(
                Chapter(
                    id = "OthersideID",
                    bookId = "1",
                    title = "Otherside",
                    imageUrl = "",
                    mediaUrl = "https://github.com/Obolrom/MusicLibrary/blob/master/rhcp_californication/red-hot-chili-peppers-otherside.mp3?raw=true",
                    duration = 4000,
                    lastTimeStamp = null,
                ),
                Chapter(
                    id = "OthersideID",
                    bookId = "1",
                    title = "Californication",
                    imageUrl = "",
                    mediaUrl = "https://github.com/Obolrom/MusicLibrary/blob/master/rhcp_californication/red-hot-chili-peppers-californication.mp3?raw=true",
                    duration = 5000,
                    lastTimeStamp = null,
                ),
            ))
        }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            when (val shit = bookApi.getUserBookLibrary()) {
                is NetworkResponse.Success -> {
                    Timber.d("asdgnnflskjfs lib is working: ${shit.body}")
                }
                is NetworkResponse.Error -> {
                    Timber.d("asdgnnflskjfs lib error: ${shit.error}")
                }
            }

//            when (val shazamSearch = shazamApi.searchByQuery("look")) {
//                is NetworkResponse.Success -> {
//                    Timber.d("shazamApi success ${shazamSearch.body}")
//                }
//
//                is NetworkResponse.Error -> {
//                    Timber.d("shazamApi error ${shazamSearch.error}")
//                }
//            }

            val input = context.resources.openRawResource(R.raw.clinteastwood_portion_mono)
                .bufferedReader()
                .use(BufferedReader::readText)
            val rawAudio = input.toRequestBody("text/plain".toMediaTypeOrNull())
            when (val shazamDetect = shazamApi.detect(rawAudio)) {
                is NetworkResponse.Success -> {
                    Timber.d("shazamApi success ${shazamDetect.body}")
                }

                is NetworkResponse.Error -> {
                    Timber.d("shazamApi error ${shazamDetect.error}")
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

    suspend fun getUserProfile() = withContext(dispatchers.io) {
        when (val response = bookApi.getUserProfile()) {
            is NetworkResponse.Success -> {
                val mappedProfile = withContext(dispatchers.computation) {
                    UserProfileMapper.map(response.body)
                }
                StatefulData.Success(mappedProfile)
            }

            is NetworkResponse.Error -> {
                StatefulData.Error(response.error ?: Exception())
            }
        }
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