package io.obolonsky.network.apihelpers.github

import timber.log.Timber

object TokenStorage {
    var accessToken: String? = null
        set(value) {
            Timber.d("fuckingFuck accessToken: $field -> $value")
            field = value
        }
    var refreshToken: String? = null
        set(value) {
            Timber.d("fuckingFuck refreshToken: $field -> $value")
            field = value
        }
    var idToken: String? = null
        set(value) {
            Timber.d("fuckingFuck idToken: $field -> $value")
            field = value
        }
}