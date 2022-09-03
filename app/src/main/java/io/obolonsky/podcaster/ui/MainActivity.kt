package io.obolonsky.podcaster.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.core.di.actions.GoToShazamAction
import io.obolonsky.core.di.actions.GoToSpaceXAction
import io.obolonsky.podcaster.PodcasterApp
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.databinding.ActivityMainFakeBinding
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : AppCompatActivity() {

    @Inject
    internal lateinit var goToShazamAction: Provider<GoToShazamAction>

    @Inject
    internal lateinit var goToSpaceXAction: Provider<GoToSpaceXAction>

    private val binding: ActivityMainFakeBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as PodcasterApp).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_fake)

        binding.goToShazam.setOnClickListener {
            goToShazamAction.get()?.navigate(this)
        }

        binding.goToSpaceX.setOnClickListener {
            goToSpaceXAction.get()?.navigate(this)
        }
    }
}