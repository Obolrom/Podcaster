package io.obolonsky.podcaster.di.modules

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.obolonsky.podcaster.viewmodels.PlayerViewModel
import io.obolonsky.podcaster.di.annotations.ViewModelKey

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(PlayerViewModel::class)
    abstract fun bindPlayerViewModel(viewModel: PlayerViewModel) : ViewModel
}