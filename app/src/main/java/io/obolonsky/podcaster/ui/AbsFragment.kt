package io.obolonsky.podcaster.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class AbsFragment(
    @LayoutRes layoutId: Int,
) : Fragment(layoutId) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModels()
        initViews(savedInstanceState)
    }

    protected abstract fun initViewModels()

    protected abstract fun initViews(savedInstanceState: Bundle?)

}