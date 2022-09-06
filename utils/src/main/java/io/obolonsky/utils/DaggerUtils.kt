package io.obolonsky.utils

import javax.inject.Provider

fun <T> Provider<T>.get(body: T.() -> Unit) {
    get()?.apply(body)
}