package io.obolonsky.podcaster.data.room

sealed class StatefulData<T>(
    val data: T? = null,
    val error: Throwable? = null
) {
    class Success<T>(data: T): StatefulData<T>(data)

    class Loading<T>(data: T? = null): StatefulData<T>(data)

    class Error<T>(throwable: Throwable, data: T? = null): StatefulData<T>(data, throwable)
}
