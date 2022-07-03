package io.obolonsky.podcaster.background

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

interface ChildWorkerFactory {

    fun create(appContext: Context, params: WorkerParameters): ListenableWorker
}