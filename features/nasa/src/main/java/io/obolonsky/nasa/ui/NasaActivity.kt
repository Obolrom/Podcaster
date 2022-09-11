package io.obolonsky.nasa.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import io.obolonsky.nasa.R
import io.obolonsky.nasa.viewmodels.ComponentViewModel

class NasaActivity : AppCompatActivity() {

    private val componentViewModel by viewModels<ComponentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        componentViewModel.component.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nasa)
    }

}