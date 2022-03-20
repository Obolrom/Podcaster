package io.obolonsky.podcaster.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import io.obolonsky.podcaster.appComponent
import io.obolonsky.podcaster.di.AppViewModelFactory
import javax.inject.Inject

abstract class AbsFragment(
    @LayoutRes layoutId: Int,
) : Fragment(layoutId) {

    @Inject
    protected lateinit var appViewModelFactory: AppViewModelFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModels()
        initViews(savedInstanceState)
    }

    protected abstract fun initViewModels()

    protected abstract fun initViews(savedInstanceState: Bundle?)

}