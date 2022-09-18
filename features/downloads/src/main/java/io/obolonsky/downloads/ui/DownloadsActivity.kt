package io.obolonsky.downloads.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import io.obolonsky.core.di.lazyViewModel
import io.obolonsky.downloads.R
import io.obolonsky.downloads.viewmodels.ComponentViewModel

class DownloadsActivity : AppCompatActivity() {

    private val componentViewModel by viewModels<ComponentViewModel>()

    private val downloadsViewModel by lazyViewModel { savedStateHandle ->
        componentViewModel.downloadComponent
            .downloadsViewModelFactory()
            .create(savedStateHandle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        componentViewModel.downloadComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
    }
}