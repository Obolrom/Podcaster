package io.obolonsky.core.di.utils

import kotlinx.coroutines.flow.Flow

interface NetworkStatusObservable {

    val statusFlow: Flow<NetworkStatus>
}