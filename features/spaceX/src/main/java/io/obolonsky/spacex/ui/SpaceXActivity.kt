package io.obolonsky.spacex.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.core.di.data.spaceX.rocket.Rocket
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.core.di.utils.NetworkStatus
import io.obolonsky.core.di.utils.NetworkStatusObservable
import io.obolonsky.spacex.R
import io.obolonsky.spacex.databinding.ActivitySpaceXBinding
import io.obolonsky.spacex.viewmodels.ComponentViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class SpaceXActivity : AppCompatActivity() {

    @Inject
    internal lateinit var networkObservable: NetworkStatusObservable

    private val componentViewModel by viewModels<ComponentViewModel>()

    private val binding by viewBinding<ActivitySpaceXBinding>()

    private val spaceXViewModel by lazyViewModel {
        componentViewModel.component
            .spaceXViewModelFactory()
            .create(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        componentViewModel.component.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_space_x)

        spaceXViewModel.rocketDetails
            .onEach(::onRocketDetails)
            .flowWithLifecycle(lifecycle)
            .launchIn(lifecycleScope)

        networkObservable.statusFlow
            .onEach(::onNetworkStatusChanged)
            .flowWithLifecycle(lifecycle)
            .launchIn(lifecycleScope)

        spaceXViewModel.loadRocketDetails("falcon9")
    }

    private fun onNetworkStatusChanged(status: NetworkStatus) {
        binding.networkStatus.text = status.name
    }

    private fun onRocketDetails(rocket: Rocket?) {
        binding.data.text = rocket.toString()
        Timber.d("spaceXRocket $rocket")
    }
}