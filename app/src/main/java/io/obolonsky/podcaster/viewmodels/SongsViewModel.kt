package io.obolonsky.podcaster.viewmodels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.obolonsky.podcaster.data.misc.MutableStateLiveData
import io.obolonsky.podcaster.data.misc.StateLiveData
import io.obolonsky.podcaster.data.repositories.SongsRepository
import io.obolonsky.podcaster.data.room.StatefulData
import io.obolonsky.podcaster.data.room.entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
): ViewModel() {

    private val _songs by lazy { MutableStateLiveData<List<Song>>() }
    val songs: StateLiveData<List<Song>> get() = _songs

    fun loadSongList() {
        viewModelScope.launch(Dispatchers.IO) {
            val items = songsRepository.getItems().map {
                Song(
                    id = it.id,
                    title = it.title ?: "no Info",
                    mediaUrl = it.mediaUrl,
                    isFavorite = false,
                )
            }
            _songs.postValue(StatefulData.Success(items))
        }
    }

    fun update(songs: List<Song>) {
        _songs.value = StatefulData.Success(songs)
    }

    fun loadSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            songsRepository.getMusicItems()
                .collect { _songs.postValue(it) }
        }
    }

}