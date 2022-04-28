package io.obolonsky.podcaster.data.misc

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import retrofit2.HttpException
import retrofit2.Response

fun <T> StateLiveData<T>.handle(
    owner: LifecycleOwner,
    successObserver: Observer<T>,
    loadingObserver: Observer<Boolean> = Observer {  },
    errorObserver: Observer<Throwable> = Observer {  }
) = observeState(owner, successObserver, loadingObserver, errorObserver)

fun <T : Any> Response<T>.handle(): T {
    return when {
        isSuccessful -> { body()!! }

        else -> throw HttpException(this)
    }
}