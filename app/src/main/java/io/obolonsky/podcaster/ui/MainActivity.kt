package io.obolonsky.podcaster.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.obolonsky.coreui.bottomNavigationSmoothVisibilityChanger
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment
        ) as NavHostFragment
    }

    private val navController by lazy { navHostFragment.navController }

    private val binding: ActivityMainBinding by viewBinding()

    private val visibilityChanger by bottomNavigationSmoothVisibilityChanger()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding.bottomBar.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.bookDetailsFragment,
                R.id.newPlayerFragment -> {
                    visibilityChanger.hideWithAnimation(binding.bottomBar)
                }

                else -> {
                    visibilityChanger.showWithAnimation(binding.bottomBar)
                }
            }
        }

        binding.bottomBar.selectedItemId = R.id.discover_dest
    }
}