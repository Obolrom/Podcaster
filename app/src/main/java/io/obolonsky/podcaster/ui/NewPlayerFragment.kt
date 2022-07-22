package io.obolonsky.podcaster.ui

import android.os.Bundle
import androidx.fragment.app.commit
import io.obolonsky.coreui.BaseFragment
import io.obolonsky.player_feature.PlayerFragment
import io.obolonsky.podcaster.R

class NewPlayerFragment : BaseFragment(R.layout.fragment_player_container) {

//    private val rewindTime by lazy {
//        resources.getInteger(R.integer.player_rewind_time) * TimeUnit.SECONDS.toMillis(1)
//    }
//
//    private val binding: FragmentPlayerBinding by viewBinding()
//    private val playerNavBinding: FragmentPlayerNavigationBinding by
//        viewBinding(viewBindingRootId = R.id.player_navigation)
//
//    private val speedArray by lazy {
//        listOf(
//            1.0f,
//            1.5f,
//            2.0f,
//            0.8f,
//        )
//    }
//
//    private var currentSpeedPosition = 0
//
//    private val List<Float>.next: Float
//        get() {
//            ++currentSpeedPosition
//            if (currentSpeedPosition == speedArray.size) currentSpeedPosition = 0
//            return this[currentSpeedPosition]
//        }

    override fun initViewModels() { }

    override fun initViews(savedInstanceState: Bundle?) {
        childFragmentManager.commit {
            add(R.id.fragment_container_view, PlayerFragment())
        }
    }
}