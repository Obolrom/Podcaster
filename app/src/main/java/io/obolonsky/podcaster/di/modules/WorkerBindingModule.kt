package io.obolonsky.podcaster.di.modules

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.obolonsky.podcaster.background.ChildWorkerFactory
import io.obolonsky.podcaster.background.TestDiWorker
import io.obolonsky.podcaster.di.WorkerKey

@Module
interface WorkerBindingModule {

    @Binds
    @IntoMap
    @WorkerKey(TestDiWorker::class)
    fun bindTestDiWorker(factory: TestDiWorker.Factory): ChildWorkerFactory
}