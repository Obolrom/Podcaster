package io.obolonsky.podcaster.ui

import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.coreui.BaseFragment
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.databinding.FragmentPlayerBinding
import io.obolonsky.podcaster.databinding.FragmentPlayerNavigationBinding
import java.util.concurrent.TimeUnit

class NewPlayerFragment : BaseFragment(R.layout.fragment_player) {

    private val rewindTime by lazy {
        resources.getInteger(R.integer.player_rewind_time) * TimeUnit.SECONDS.toMillis(1)
    }

    private val binding: FragmentPlayerBinding by viewBinding()
    private val playerNavBinding: FragmentPlayerNavigationBinding by
        viewBinding(viewBindingRootId = R.id.player_navigation)

    private val speedArray by lazy {
        listOf(
            1.0f,
            1.5f,
            2.0f,
            0.8f,
        )
    }

    private var currentSpeedPosition = 0

    private val List<Float>.next: Float
        get() {
            ++currentSpeedPosition
            if (currentSpeedPosition == speedArray.size) currentSpeedPosition = 0
            return this[currentSpeedPosition]
        }

    override fun initViewModels() { }

    override fun initViews(savedInstanceState: Bundle?) {

    }
}