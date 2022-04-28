package io.obolonsky.podcaster.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.obolonsky.podcaster.data.misc.MutableStateLiveData
import io.obolonsky.podcaster.data.misc.StateLiveData
import io.obolonsky.podcaster.data.repositories.SongsRepository
import io.obolonsky.podcaster.data.room.entities.Song
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
): ViewModel() {

    private val _songs by lazy { MutableStateLiveData<List<Song>>() }
    val songs: StateLiveData<List<Song>> get() = _songs
}