package io.obolonsky.player_feature

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import io.obolonsky.core.di.actions.ShowPlayer
import javax.inject.Inject

class ShowPlayerFragmentAction : ShowPlayer {

    override fun showPlayer(fragmentManager: FragmentManager) {
        fragmentManager.commit {
            addToBackStack(null)
            add(R.id.container, PlayerFragment())
        }
    }
}