package io.obolonsky.network.utils

import io.obolonsky.core.di.scopes.ApplicationScope
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.reactivex.rxjava3.core.Scheduler
import kotlinx.coroutines.rx3.asScheduler
import javax.inject.Inject

@ApplicationScope
class RxSchedulersImpl @Inject constructor(
    coroutineSchedulers: CoroutineSchedulers,
) : RxSchedulers {

    override val io: Scheduler = coroutineSchedulers.io.asScheduler()

    override val computation: Scheduler = coroutineSchedulers.computation.asScheduler()
}