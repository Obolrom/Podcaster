package io.obolonsky.spacex.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.core.di.data.spaceX.rocket.Rocket
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.spacex.viewmodels.ComponentViewModel
import io.obolonsky.spacex.R
import io.obolonsky.spacex.databinding.ActivitySpaceXBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

class SpaceXActivity : AppCompatActivity() {

    private val componentViewModel by viewModels<ComponentViewModel>()

    private val binding by viewBinding<ActivitySpaceXBinding>()

    private val spaceXViewModel by lazyViewModel {
        componentViewModel.component
            .spaceXViewModelFactory()
            .create(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_space_x)

        lifecycleScope.launch {
            spaceXViewModel.rocketDetails
                .onEach(::onRocketDetails)
                .flowWithLifecycle(lifecycle)
                .collect()
        }

        spaceXViewModel.loadRocketDetails("falcon9")
    }

    private fun onRocketDetails(rocket: Rocket?) {
        binding.data.text = rocket.toString()
        Timber.d("spaceXRocket $rocket")
    }
}