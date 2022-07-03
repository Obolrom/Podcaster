package io.obolonsky.podcaster.background

//@HiltWorker
//class TestDiWorker @AssistedInject constructor(
//    @Assisted context: Context,
//    @Assisted params: WorkerParameters,
//    private val bookApi: BookApi
//) : CoroutineWorker(context, params) {
//
//    override suspend fun doWork(): Result {
//        val resp = bookApi.getUserProfile()
//        return Result.success()
//    }
//}