package io.obolonsky.nasa.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.nasa.R
import io.obolonsky.nasa.databinding.ActivityNasaBinding
import io.obolonsky.nasa.viewmodels.ComponentViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class NasaActivity : AppCompatActivity() {

    private val componentViewModel by viewModels<ComponentViewModel>()

    private val binding by viewBinding<ActivityNasaBinding>()

    private val nasaViewModel by lazyViewModel {
        componentViewModel.component
            .nasaViewModelFactory()
            .create(it)
    }

    private val apodImagesAdapter by lazy {
        ApodImagesAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        componentViewModel.component.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nasa)

        binding.viewPager.adapter = apodImagesAdapter

        nasaViewModel.apodImageUrls
            .onEach(apodImagesAdapter::submitList)
            .flowWithLifecycle(lifecycle)
            .launchIn(lifecycleScope)

        nasaViewModel.loadApodImageUrls(4)
    }

}