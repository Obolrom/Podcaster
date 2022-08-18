package io.obolonsky.core.di.common

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

fun Flow<*>.launchWhenStarted(
    lifecycleScope: LifecycleCoroutineScope
) = lifecycleScope.launchWhenStarted {
    collect()
}