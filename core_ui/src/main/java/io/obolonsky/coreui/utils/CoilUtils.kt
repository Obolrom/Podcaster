package io.obolonsky.coreui.utils

import coil.decode.DataSource
import coil.request.ImageResult
import coil.request.SuccessResult
import coil.transition.CrossfadeTransition
import coil.transition.Transition
import coil.transition.TransitionTarget

class CrossFadeTransitionFactory(
    private val durationMills: Int = 400,
): Transition.Factory {

    override fun create(target: TransitionTarget, result: ImageResult): Transition {
        val cross = CrossfadeTransition.Factory(durationMills)

        return Transition.Factory { transitionTarget, imageResult ->
            cross.create(
                target = transitionTarget,
                result = (imageResult as? SuccessResult)?.takeIf {
                    it.dataSource == DataSource.MEMORY_CACHE
                }?.run {
                    copy(dataSource = DataSource.MEMORY)
                } ?: imageResult
            )
        }.create(target, result)
    }
}

