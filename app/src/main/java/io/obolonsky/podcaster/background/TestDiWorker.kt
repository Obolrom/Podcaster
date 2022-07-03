package io.obolonsky.podcaster.background

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.podcaster.api.BookApi
import javax.inject.Inject
import javax.inject.Provider

class TestDiWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val bookApi: BookApi
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val resp = bookApi.getUserProfile()
        return Result.success()
    }

//    @AssistedFactory
//    interface Factory {
//        fun create(appCtx: Context, params: WorkerParameters): TestDiWorker
//    }
    class Factory @Inject constructor(
        private val foo: Provider<BookApi>
    ) : ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return TestDiWorker(
                appContext,
                params,
                foo.get()
            )
        }
    }
}