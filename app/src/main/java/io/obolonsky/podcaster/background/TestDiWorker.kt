package io.obolonsky.podcaster.background

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.podcaster.api.BookApi

class TestDiWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val bookApi: BookApi
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return Result.success()
    }

//    @AssistedFactory
//    interface Factory {
//        fun create(appCtx: Context, params: WorkerParameters): TestDiWorker
//    }
    @AssistedFactory
    interface Factory : ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): TestDiWorker
    }
}