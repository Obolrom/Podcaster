package io.obolonsky.player.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.core.di.actions.GetDownloadServiceClassAction
import io.obolonsky.core.di.common.AudioSource
import io.obolonsky.core.di.downloads.Downloader
import io.obolonsky.player.di.ScopedShazamRepo
import io.obolonsky.player.redux.PlayerUiState
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

@Suppress("unused_parameter")
internal class DownloadViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val downloader: Downloader,
    private val shazamRepository: ScopedShazamRepo,
    private val getDownloadServiceClassAction: GetDownloadServiceClassAction,
) : ViewModel(), ContainerHost<PlayerUiState, Unit> {

    override val container: Container<PlayerUiState, Unit> = container(
        initialState = PlayerUiState(
            tracks = null,
        )
    )

    init {
        intent {
            reduce { state.copy(tracks = AudioSource.tracks) }
        }
    }

    fun updateCurrentTrack(index: Int) = intent {
        val currentPlaying = state.tracks?.get(index)

        reduce {
            state.copy(currentPlaying = currentPlaying)
        }
    }

    fun download(currentIndex: Int) {
        AudioSource.tracks
            .getOrNull(currentIndex)
            ?.let {
                viewModelScope.launch {
                    shazamRepository.saveTrack(it)
                }
            }

        AudioSource.mediaItems
            .getOrNull(currentIndex)
            ?.let {
                downloader.toggleDownload(
                    mediaItem = it,
                    serviceClass = getDownloadServiceClassAction.downloadServiceClass,
                )
            }
    }

    override fun onCleared() {
        downloader.release()
        super.onCleared()
    }

    @AssistedFactory
    interface Factory {

        fun create(savedStateHandle: SavedStateHandle): DownloadViewModel
    }
}