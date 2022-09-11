package io.obolonsky.player.actions

import io.obolonsky.core.di.actions.CreatePlayerScreenAction
import io.obolonsky.player.PlayerFragment
import javax.inject.Inject

class ShowPlayerActionImpl @Inject constructor() : CreatePlayerScreenAction {

    override fun showPlayer() = PlayerFragment()
}