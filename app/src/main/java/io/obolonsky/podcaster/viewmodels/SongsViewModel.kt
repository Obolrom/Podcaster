package io.obolonsky.podcaster.viewmodels

import androidx.lifecycle.*
import io.obolonsky.podcaster.data.repositories.SongsRepository
import javax.inject.Inject

class SongsViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
): ViewModel() {

    fun loadSongs() = songsRepository.getMusicItems().asLiveData()
}