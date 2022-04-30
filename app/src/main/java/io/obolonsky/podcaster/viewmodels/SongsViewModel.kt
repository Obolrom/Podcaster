package io.obolonsky.podcaster.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.obolonsky.podcaster.data.misc.MutableStateLiveData
import io.obolonsky.podcaster.data.misc.StateLiveData
import io.obolonsky.podcaster.data.repositories.SongsRepository
import io.obolonsky.podcaster.data.room.entities.Book
import io.obolonsky.podcaster.data.room.entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
): ViewModel() {

    private val _songs by lazy { MutableStateLiveData<List<Song>>() }
    val songs: StateLiveData<List<Song>> get() = _songs

    private val _books by lazy { MutableLiveData<PagingData<Book>>() }
    val books: LiveData<PagingData<Book>> get() = _books

    fun loadBooks() {
        songsRepository.loadBooks(
            PagingConfig(pageSize = 15)
        )
            .cachedIn(viewModelScope)
            .onEach { _books.postValue(it) }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }
}