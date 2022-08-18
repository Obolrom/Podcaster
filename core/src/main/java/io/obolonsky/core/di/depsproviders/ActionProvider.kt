package io.obolonsky.core.di.depsproviders

import io.obolonsky.core.di.actions.ShowPlayer

interface ActionProvider {

    fun providePlayerAction(): ShowPlayer
}