package io.obolonsky.podcaster.background

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.podcaster.api.BookApi
import io.obolonsky.podcaster.data.repositories.SongsRepository

class AnotherOneWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val bookApi: BookApi,
    private val repo: SongsRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val resp = bookApi.getUserProfile()
        repo.chapters
        return Result.success()
    }

    @AssistedFactory
    interface Factory : ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): AnotherOneWorker
    }
}