package io.obolonsky.podcaster.ui

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
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

    private var offsetAnimator: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding.bottomBar.setupWithNavController(navController)

        binding.bottomBar.menu.findItem(R.id.favorites).isChecked = true

        binding.bottomBar
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.bookDetailsFragment -> {
                    binding.bottomBar.hideWithAnimation()
                }

                else -> {
                    binding.bottomBar.showWithAnimation()
                }
            }
        }

        binding.bottomBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.favorites -> {
                    binding.bottomBar.visibility = View.GONE
                }

                else -> { }
            }
            true
        }

        binding.bottomBar.selectedItemId = R.id.discover_dest
    }

    private fun View.showWithAnimation() = animateBarVisibility(this, true)

    private fun View.hideWithAnimation() = animateBarVisibility(this, false)

    private fun animateBarVisibility(child: View, isVisible: Boolean) {
        if (offsetAnimator == null) {
            offsetAnimator = ValueAnimator().apply {
                interpolator = DecelerateInterpolator()
                duration = 250L
            }

            offsetAnimator?.addUpdateListener {
                child.translationY = it.animatedValue as Float
            }
        } else {
            offsetAnimator?.cancel()
            child.clearAnimation()
        }

        val targetTranslation = if (isVisible) 0f else child.height.toFloat()
        offsetAnimator?.apply {
            setFloatValues(child.translationY, targetTranslation)
            start()
        }
    }
}