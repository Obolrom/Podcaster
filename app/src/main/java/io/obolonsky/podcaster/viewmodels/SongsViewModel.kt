package io.obolonsky.podcaster.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.podcaster.data.repositories.SongsRepository
import io.obolonsky.podcaster.data.room.StatefulData
import io.obolonsky.podcaster.data.room.entities.Book
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SongsViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val songsRepository: SongsRepository,
    private val dispatchers: CoroutineSchedulers,
) : ViewModel() {

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
            .flowOn(dispatchers.io)
            .launchIn(viewModelScope)
    }

    fun loadBook(id: String) {
        viewModelScope.launch(dispatchers.io) {
            _book.emit(StatefulData.Loading())
            _book.emit(songsRepository.loadBook(id))
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle): SongsViewModel
    }
}