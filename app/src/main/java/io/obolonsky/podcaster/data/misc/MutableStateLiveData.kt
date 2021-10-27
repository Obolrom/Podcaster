package io.obolonsky.podcaster.data.misc

import io.obolonsky.podcaster.data.room.StatefulData

class MutableStateLiveData<T> : StateLiveData<T>() {

    public override fun postValue(value: StatefulData<T>?) {
        super.postValue(value)
    }

    public override fun setValue(value: StatefulData<T>?) {
        super.setValue(value)
    }

}