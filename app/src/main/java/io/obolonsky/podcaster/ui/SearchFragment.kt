package io.obolonsky.podcaster.ui

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.databinding.FragmentSearchBinding
import io.obolonsky.podcaster.ui.adapters.BookSearchAdapter
import io.obolonsky.podcaster.ui.adapters.OffsetItemDecorator
import io.obolonsky.podcaster.viewmodels.SearchViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SearchFragment : AbsFragment(R.layout.fragment_search) {

    private val searchViewModel by viewModels<SearchViewModel>()

    private val binding by viewBinding<FragmentSearchBinding>()

    private val searchPagingAdapter = BookSearchAdapter()

    override fun initViewModels() {
        lifecycleScope.launchWhenStarted {
            searchViewModel.searchBooks
                .onEach { searchPagingAdapter.submitList(it) }
                .collect()
        }
    }

    override fun initViews(savedInstanceState: Bundle?) {
        binding.recyclerSearch.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchPagingAdapter

            addItemDecoration(
                OffsetItemDecorator(resources.getDimensionPixelOffset(R.dimen.big_margin))
            )
        }

        searchViewModel.searchSubscribe()

        lifecycleScope.launchWhenStarted {
            searchViewModel.searchQuery.value = "flo"
        }
    }
}