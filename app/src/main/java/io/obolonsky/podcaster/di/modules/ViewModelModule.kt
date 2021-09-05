package io.obolonsky.podcaster.di.modules

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.obolonsky.podcaster.viewmodels.PlayerViewModel
import io.obolonsky.podcaster.di.annotations.ViewModelKey
import io.obolonsky.podcaster.viewmodels.SongsViewModel

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(PlayerViewModel::class)
    abstract fun bindPlayerViewModel(viewModel: PlayerViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SongsViewModel::class)
    abstract fun bindSongsViewModel(viewModel: SongsViewModel) : ViewModel
}