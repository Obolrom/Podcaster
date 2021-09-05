package io.obolonsky.podcaster.data.room

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

fun <ResultType, RequestType> load(
    query: () -> Flow<ResultType>,
    fetch: suspend () -> RequestType,
    saveFetchResult: suspend (RequestType) -> Unit,
    shouldFetch: (ResultType) -> Boolean = { true },
) = flow {
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(Resource.Loading(data))

        try {
            saveFetchResult(fetch())
            query().map { Resource.Success(it) }
        } catch (error: Throwable) {
            query().map { Resource.Error(error, it) }
        }
    } else {
        query().map { Resource.Success(it) }
    }

    emitAll(flow)
}.flowOn(Dispatchers.IO)