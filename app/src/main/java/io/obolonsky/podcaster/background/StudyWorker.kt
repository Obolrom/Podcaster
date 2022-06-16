package io.obolonsky.podcaster.background

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.obolonsky.podcaster.data.repositories.SongsRepository
import io.obolonsky.podcaster.di.modules.CoroutineSchedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class StudyWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        val id = inputData.keyValueMap.entries
            .map { (_, value) -> value }

        Timber.d("studyWorker do work id: $id")

        Result.success()
    }
}