package io.obolonsky.podcaster.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.obolonsky.podcaster.data.repositories.SongsRepository
import io.obolonsky.podcaster.di.modules.CoroutineSchedulers
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SongsRepository,
    private val dispatchers: CoroutineSchedulers,
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    fun searchSubscribe() {

    }
}