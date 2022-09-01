package io.obolonsky.player.actions

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import io.obolonsky.core.di.actions.ShowPlayer
import io.obolonsky.player.PlayerFragment
import io.obolonsky.player.R
import javax.inject.Inject

class GoToPlayerAction @Inject constructor() : ShowPlayer {

    override fun showPlayer(fragmentManager: FragmentManager) {
        fragmentManager.commit {
            addToBackStack(null)
            add(R.id.container, PlayerFragment())
        }
    }
}