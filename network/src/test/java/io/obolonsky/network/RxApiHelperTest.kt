package io.obolonsky.network

import io.obolonsky.network.utils.RxSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

abstract class RxApiHelperTest {

    val testRxSchedulers by lazy {
        val testRxScheduler = Schedulers.trampoline()
        object : RxSchedulers {
            override val io = testRxScheduler
            override val computation = testRxScheduler
        }
    }
}