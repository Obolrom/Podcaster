package io.obolonsky.network

import io.obolonsky.core.di.utils.CoroutineSchedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalCoroutinesApi::class)
abstract class CoroutineApiHelperTest {

    fun provideTestCoroutineDispatcher(
        testScheduler: TestCoroutineScheduler
    ): CoroutineSchedulers {
        val testCoroutineScheduler = StandardTestDispatcher(testScheduler)
        return object : CoroutineSchedulers {
            override val main = testCoroutineScheduler
            override val io = testCoroutineScheduler
            override val computation = testCoroutineScheduler
            override val unconfined = testCoroutineScheduler
        }
    }
}