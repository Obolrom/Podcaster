package io.obolonsky.podcaster.di.injectors

import io.obolonsky.podcaster.ui.PlayerFragment

interface Injector {
    fun inject(target: PlayerFragment)
}