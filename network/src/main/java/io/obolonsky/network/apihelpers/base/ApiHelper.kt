package io.obolonsky.network.apihelpers.base

import io.obolonsky.core.di.Reaction
import kotlinx.coroutines.flow.Flow

interface ApiHelper<R, P> {

    suspend fun load(param: P): Reaction<R>
}

/**
 * Base api helper that gives loads data with [Flow]
 */
interface FlowApiHelper<P, R> {

    fun load(param: P): Flow<Reaction<R>>
}

/**
 * Api helper that by contract emits only SINGLE value
 */
interface SingleFlowApiHelper<P, R> : FlowApiHelper<P, R>