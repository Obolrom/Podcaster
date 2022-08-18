package io.obolonsky.podcaster.misc

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import io.obolonsky.podcaster.PodcasterApp
import io.obolonsky.podcaster.di.components.AppComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
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

// TODO: remove
fun Flow<*>.launchWhenStarted(
    lifecycleScope: LifecycleCoroutineScope
) = lifecycleScope.launchWhenStarted {
    collect()
}

val Fragment.appComponent: AppComponent
    get() = (requireActivity().application as PodcasterApp).appComponent

val AppCompatActivity.appComponent: AppComponent
    get() = (application as PodcasterApp).appComponent