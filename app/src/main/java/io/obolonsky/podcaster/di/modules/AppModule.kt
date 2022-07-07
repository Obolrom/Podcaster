package io.obolonsky.podcaster.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import io.obolonsky.podcaster.di.scopes.ApplicationScope
import io.obolonsky.podcaster.player.MusicServiceConnection

@Module(includes = [
    DatabaseModule::class,
    WebServiceModule::class,
    PlayerModule::class,
    BinderModule::class,
    WorkerBindingModule::class,
])
class AppModule {

    @ApplicationScope
    @Provides
    fun provideMusicServiceConnection(
        context: Context
    ) = MusicServiceConnection(context)
}