package io.obolonsky.podcaster.ui

import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.data.room.entities.Book
import io.obolonsky.podcaster.databinding.FragmentSearchBinding
import io.obolonsky.podcaster.misc.launchWhenStarted
import io.obolonsky.podcaster.ui.adapters.BookSearchAdapter
import io.obolonsky.podcaster.ui.adapters.OffsetItemDecorator
import io.obolonsky.podcaster.viewmodels.SearchViewModel
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SearchFragment : AbsFragment(R.layout.fragment_search) {

    private val searchViewModel by viewModels<SearchViewModel>()

    private val binding by viewBinding<FragmentSearchBinding>()

    private val searchPagingAdapter = BookSearchAdapter(onClick = ::onBookClicked)

    override fun initViewModels() {
        searchViewModel.searchBooks
            .onEach { searchPagingAdapter.submitList(it) }
            .launchWhenStarted(lifecycleScope)
    }

    override fun initViews(savedInstanceState: Bundle?) {
        binding.searchInput.addTextChangedListener(
            onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
                text?.let { searchViewModel.searchQuery.value = it.toString() }
            }
        )

        binding.recyclerSearch.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchPagingAdapter

            addItemDecoration(
                OffsetItemDecorator(resources.getDimensionPixelOffset(R.dimen.big_margin))
            )
        }

        searchViewModel.searchSubscribe()
    }

    private fun onBookClicked(book: Book) {
        val action = SearchFragmentDirections
            .actionSearchFragmentToBookDetailsFragment(book.id)
        findNavController().navigate(action)
    }
}