package io.obolonsky.network.utils

internal fun String.fieldShouldNotBeNull(): Nothing = error("`$this` should not be null")