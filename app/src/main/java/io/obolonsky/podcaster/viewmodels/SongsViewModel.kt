package io.obolonsky.podcaster.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.obolonsky.podcaster.data.repositories.SongsRepository
import io.obolonsky.podcaster.data.responses.MusicItem
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SongsViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
): ViewModel() {

    private val _songs = MutableLiveData<List<MusicItem>?>()
    val songs: LiveData<List<MusicItem>?> = _songs

    fun loadSongs() {
        viewModelScope.launch {
            songsRepository.getMusicItems()
                .catch { Timber.e(it) }
                .collect { songs ->
                    _songs.value = songs
                }
        }
    }
}