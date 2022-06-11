package io.obolonsky.podcaster.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.obolonsky.podcaster.data.repositories.SongsRepository
import io.obolonsky.podcaster.data.room.entities.Book
import io.obolonsky.podcaster.di.modules.CoroutineSchedulers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SongsRepository,
    private val dispatchers: CoroutineSchedulers,
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    private val _searchBooks by lazy {
        MutableSharedFlow<List<Book>>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    }
    val searchBooks = _searchBooks.asSharedFlow()

    private var searchJob: Job? = null

    @OptIn(FlowPreview::class)
    fun searchSubscribe() {
        searchQuery
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .debounce(300)
            .onEach { performSearchQuery(it) }
            .flowOn(dispatchers.computation)
            .launchIn(viewModelScope)
    }

    private fun performSearchQuery(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch(dispatchers.io) {
            val queryResponse = searchRepository.search(query).distinctBy { it.id }
            _searchBooks.emit(queryResponse)
        }
    }
}