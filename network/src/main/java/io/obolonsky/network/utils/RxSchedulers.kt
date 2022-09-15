package io.obolonsky.network.utils

import io.reactivex.rxjava3.core.Scheduler

interface RxSchedulers {

    val io: Scheduler

    val computation: Scheduler
}