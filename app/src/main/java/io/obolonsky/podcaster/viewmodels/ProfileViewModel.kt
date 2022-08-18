package io.obolonsky.podcaster.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.podcaster.data.repositories.SongsRepository
import io.obolonsky.podcaster.data.room.StatefulData
import io.obolonsky.podcaster.data.room.entities.UserProfile
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
    private val dispatchers: CoroutineSchedulers,
) : ViewModel() {

    private val _userProfile by lazy {
        MutableSharedFlow<StatefulData<UserProfile>>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    }
    val userProfile: SharedFlow<StatefulData<UserProfile>>
        get() = _userProfile.asSharedFlow()

    fun fetchUserInfo() {
        viewModelScope.launch(dispatchers.io) {
            _userProfile.emit(
                value = songsRepository.getUserProfile()
            )
        }
    }
}