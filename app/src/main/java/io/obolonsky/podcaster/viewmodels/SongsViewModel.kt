package io.obolonsky.podcaster.viewmodels

import androidx.lifecycle.*
import io.obolonsky.podcaster.data.misc.MutableStateLiveData
import io.obolonsky.podcaster.data.misc.StateLiveData
import io.obolonsky.podcaster.data.repositories.SongsRepository
import io.obolonsky.podcaster.data.room.entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class SongsViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
): ViewModel() {

    private val _songs by lazy { MutableStateLiveData<List<Song>>() }
    val songs: StateLiveData<List<Song>> get() = _songs

    fun loadSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            songsRepository.getMusicItems()
                .collect { _songs.postValue(it) }
        }
    }

}