package io.obolonsky.downloads

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import io.obolonsky.downloads.viewmodels.ComponentViewModel

class DownloadsActivity : AppCompatActivity() {

    private val componentViewModel by viewModels<ComponentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        componentViewModel.downloadComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
    }
}