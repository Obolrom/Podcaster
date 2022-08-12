package io.obolonsky.core.di

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner

@Suppress("unchecked_cast")
class ViewModelFactory<T : ViewModel>(
    savedStateRegistryOwner: SavedStateRegistryOwner,
    private val create: (stateHandle: SavedStateHandle) -> T,
) : AbstractSavedStateViewModelFactory(savedStateRegistryOwner, null) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return create.invoke(handle) as T
    }
}

inline fun <reified T : ViewModel> Fragment.lazyViewModel(
    noinline create: (savedStateHandle: SavedStateHandle) -> T,
) = viewModels<T> {
    ViewModelFactory(this, create)
}

inline fun <reified T : ViewModel> AppCompatActivity.lazyViewModel(
    noinline create: (savedStateHandle: SavedStateHandle) -> T,
) = viewModels<T> {
    ViewModelFactory(this, create)
}