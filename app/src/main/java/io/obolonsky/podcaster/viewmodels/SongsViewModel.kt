package io.obolonsky.podcaster.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.obolonsky.podcaster.data.repositories.SongsRepository
import io.obolonsky.podcaster.data.room.StatefulData
import io.obolonsky.podcaster.data.room.entities.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
): ViewModel() {

    private val _books by lazy {
        MutableSharedFlow<PagingData<Book>>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    }
    val books: SharedFlow<PagingData<Book>> get() = _books.asSharedFlow()

    private val _book by lazy { MutableSharedFlow<StatefulData<Book>>() }
    val book: SharedFlow<StatefulData<Book>> get() = _book.asSharedFlow()

    fun loadBooks() {
        songsRepository.loadBooks(
            PagingConfig(pageSize = 15)
        )
            .cachedIn(viewModelScope)
            .onEach { _books.emit(it) }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun loadBook(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _book.emit(StatefulData.Loading())
            _book.emit(songsRepository.loadBook(id))
        }
    }
}