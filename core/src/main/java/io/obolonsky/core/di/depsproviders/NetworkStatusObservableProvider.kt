package io.obolonsky.core.di.depsproviders

import io.obolonsky.core.di.utils.NetworkStatusObservable

interface NetworkStatusObservableProvider {

    val networkStatusObservable: NetworkStatusObservable
}