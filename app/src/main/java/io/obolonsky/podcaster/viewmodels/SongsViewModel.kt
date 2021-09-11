package io.obolonsky.podcaster.viewmodels

import androidx.lifecycle.*
import io.obolonsky.podcaster.data.repositories.SongsRepository
import io.obolonsky.podcaster.data.room.Resource
import io.obolonsky.podcaster.data.room.entities.Song
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class SongsViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
): ViewModel() {

    private val _songs: MutableLiveData<Resource<List<Song>>?> = MutableLiveData()
    val songs: LiveData<Resource<List<Song>>?> = _songs

    fun loadSongs(shouldFetch: Boolean = true) {
        viewModelScope.launch {
            songsRepository.getMusicItems(shouldFetch)
                .collect { _songs.value = it }
        }
    }
}