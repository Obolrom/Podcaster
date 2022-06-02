package io.obolonsky.podcaster.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding.bottomBar.setupWithNavController(navController)
        binding.bottomBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.discover_dest -> {

                }

                else -> { }
            }
            true
        }
    }
}