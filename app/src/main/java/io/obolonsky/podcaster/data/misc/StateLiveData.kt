package io.obolonsky.podcaster.data.misc

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.obolonsky.podcaster.data.room.StatefulData

open class StateLiveData<T> : LiveData<StatefulData<T>>() {

    fun observeState(
        owner: LifecycleOwner,
        successObserver: Observer<T>,
        loadingObserver: Observer<Boolean> = Observer {  },
        errorObserver: Observer<Throwable> = Observer {  }
    ) = observe(owner, StateObserver(successObserver, loadingObserver, errorObserver))

    private inner class StateObserver(
        private val successObserver: Observer<T>,
        private val loadingObserver: Observer<Boolean>,
        private val errorObserver: Observer<Throwable>,
    ) : Observer<StatefulData<T>> {

        override fun onChanged(t: StatefulData<T>?) {
            t?.let { data ->
                when (data) {
                    is StatefulData.Success -> {
                        loadingObserver.onChanged(false)
                        successObserver.onChanged(data.data)
                    }

                    is StatefulData.Loading -> {
                        data.data?.let { successObserver.onChanged(it) }
                        loadingObserver.onChanged(true)
                    }

                    is StatefulData.Error -> {
                        loadingObserver.onChanged(false)
                        errorObserver.onChanged(data.error)
                    }
                }
            }
        }

    }

}