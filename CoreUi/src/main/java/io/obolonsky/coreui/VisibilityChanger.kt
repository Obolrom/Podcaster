package io.obolonsky.coreui

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.properties.ReadOnlyProperty

interface VisibilityChanger {

    fun showWithAnimation(view: View)

    fun hideWithAnimation(view: View)
}

fun bottomNavigationSmoothVisibilityChanger(animationDuration: Long = 250L) =
    ReadOnlyProperty<Any, VisibilityChanger> { _, _ ->
        return@ReadOnlyProperty BottomNavigationSmoothVisibilityChanger(animationDuration)
    }


internal class BottomNavigationSmoothVisibilityChanger(
    private val animationDuration: Long,
) : VisibilityChanger {

    private var offsetAnimator: ValueAnimator? = null

    override fun showWithAnimation(view: View) = animateBarVisibility(view, true)

    override fun hideWithAnimation(view: View) = animateBarVisibility(view, false)

    private fun animateBarVisibility(child: View, isVisible: Boolean) {
        if (offsetAnimator == null) {
            offsetAnimator = ValueAnimator().apply {
                interpolator = DecelerateInterpolator()
                duration = animationDuration
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