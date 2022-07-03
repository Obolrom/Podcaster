package io.obolonsky.podcaster.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.data.room.entities.Book
import io.obolonsky.podcaster.databinding.FragmentSearchBinding
import io.obolonsky.podcaster.misc.launchWhenStarted
import io.obolonsky.podcaster.ui.adapters.BookSearchAdapter
import io.obolonsky.podcaster.ui.adapters.GridItemDecoration
import io.obolonsky.podcaster.viewmodels.SearchViewModel
import kotlinx.coroutines.flow.onEach

class SearchFragment : AbsFragment(R.layout.fragment_search) {

    private val searchViewModel by viewModels<SearchViewModel>()

    private val binding by viewBinding<FragmentSearchBinding>()

    private val searchPagingAdapter = BookSearchAdapter(onClick = ::onBookClicked)

    override fun initViewModels() {
        searchViewModel.searchBooks
            .onEach { contentVisibility(it.size) }
            .onEach { searchPagingAdapter.submitList(it) }
            .launchWhenStarted(lifecycleScope)
    }

    override fun initViews(savedInstanceState: Bundle?) {
        binding.searchInput.addTextChangedListener(
            onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
                text?.let { searchViewModel.searchQuery.value = it.toString() }
            }
        )

        binding.filter.setOnClickListener {
            AlertDialog.Builder(it.context)
                .setTitle("Pick a color")
                .setSingleChoiceItems(
                    arrayOf("first", "second"),
                    0
                ) { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.firstPublication.load(
            "https://wallpaperaccess.com/full/2147116.jpg"
        ) {
            crossfade(400)
        }

        binding.secondPublication.load(
            "https://ae01.alicdn.com/kf/HTB1qc.YborrK1RkSne1q6ArVVXa6/90x150cm-rock-and-roll-flag-banner-and-with-punk-custom-any-rock-punk-flag.jpg_Q90.jpg_.webp"
        ) {
            crossfade(400)
        }

        binding.thirdPublication.load(
            "https://www.fopp.com/wp-content/uploads/2022/02/PostPunk_735x315_v2.jpg"
        ) {
            crossfade(400)
        }

        binding.recyclerSearch.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = searchPagingAdapter

            addItemDecoration(
                GridItemDecoration(resources.getDimensionPixelOffset(R.dimen.big_margin), 2)
            )
        }

        searchViewModel.searchSubscribe()
    }

    private fun onBookClicked(book: Book) {
        val action = SearchFragmentDirections
            .actionSearchFragmentToBookDetailsFragment(book.id)
        findNavController().navigate(action)
    }

    private fun contentVisibility(searchResultsCount: Int) {
        if (searchResultsCount == 0) {
            changeSearchResultsVisibility(View.GONE)
            changeGenresVisibility(View.VISIBLE)
        } else {
            changeGenresVisibility(View.GONE)
            changeSearchResultsVisibility(View.VISIBLE)
        }
    }

    private fun changeSearchResultsVisibility(visibility: Int) {
        binding.searchResults.visibility = visibility
    }

    private fun changeGenresVisibility(visibility: Int) {
        binding.genres.visibility = visibility
        binding.fP.visibility = visibility
        binding.sP.visibility = visibility
        binding.tP.visibility = visibility
    }
}