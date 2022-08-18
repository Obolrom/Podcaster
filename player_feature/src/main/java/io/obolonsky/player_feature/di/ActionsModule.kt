package io.obolonsky.player_feature.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import io.obolonsky.core.di.actions.ShowPlayer
import io.obolonsky.player_feature.ShowPlayerFragmentAction

@Module
class ActionsModule {

    @Provides
    fun provideShowPlayerFragmentAction(): ShowPlayer = ShowPlayerFragmentAction()

//    @Binds
//    abstract fun provideShowPlayerFragmentAction(action: ShowPlayerFragmentAction): ShowPlayer
}