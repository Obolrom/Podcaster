package io.obolonsky.podcaster.di.modules

import androidx.work.ListenableWorker
import dagger.Module
import dagger.multibindings.Multibinds
import io.obolonsky.podcaster.background.ChildWorkerFactory

@Module
@Suppress("unused")
interface WorkerBindingModule {

    @Multibinds
    fun provideWorkersMap(): Map<Class<out ListenableWorker>, ChildWorkerFactory>
}