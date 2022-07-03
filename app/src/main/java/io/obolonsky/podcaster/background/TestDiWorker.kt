package io.obolonsky.podcaster.background

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.obolonsky.podcaster.api.BookApi

@HiltWorker
class TestDiWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val bookApi: BookApi
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return Result.success()
    }
}