package io.obolonsky.podcaster.misc

import java.io.InputStream

fun Int?.orZero() = this ?: 0

fun Long?.orZero() = this ?: 0L

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}

fun InputStream.getMegaBytes(): String {
    val bytes = this.available().toDouble()
    val kiloBytes = bytes.div(1000)
    val megaBytes = kiloBytes.div(1000)

    return "${megaBytes.round(2)} mb"
}