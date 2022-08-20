package io.obolonsky.podcaster.actions

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import io.obolonsky.core.di.actions.ShowPlayer
import io.obolonsky.player_feature.PlayerFragment
import io.obolonsky.player_feature.R
import javax.inject.Inject

class GoToPlayerAction @Inject constructor() : ShowPlayer {

    override fun showPlayer(fragmentManager: FragmentManager) {
        fragmentManager.commit {
            addToBackStack(null)
            add(R.id.container, PlayerFragment())
        }
    }
}