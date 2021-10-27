package io.obolonsky.podcaster.data.misc

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

fun <T> StateLiveData<T>.handle(
    owner: LifecycleOwner,
    successObserver: Observer<T>,
    loadingObserver: Observer<Boolean> = Observer {  },
    errorObserver: Observer<Throwable> = Observer {  }
) = observeState(owner, successObserver, loadingObserver, errorObserver)