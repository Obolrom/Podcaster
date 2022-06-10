package io.obolonsky.podcaster.ui

import android.os.Bundle
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.databinding.FragmentSearchBinding
import io.obolonsky.podcaster.viewmodels.SearchViewModel

@AndroidEntryPoint
class SearchFragment : AbsFragment(R.layout.fragment_search) {

    private val searchViewModel by viewModels<SearchViewModel>()

    private val binding by viewBinding<FragmentSearchBinding>()

    override fun initViewModels() {

    }

    override fun initViews(savedInstanceState: Bundle?) {

    }
}