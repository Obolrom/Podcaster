package io.obolonsky.podcaster.data.room

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType> load(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
) = flow {
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(StatefulData.Loading(data))

        try {
            saveFetchResult(fetch())
            query().map { StatefulData.Success(it) }
        } catch (error: Throwable) {
            query().map { StatefulData.Error(error, it) }
        }
    } else {
        query().map { StatefulData.Success(it) }
    }

    emitAll(flow)
}.flowOn(Dispatchers.IO)