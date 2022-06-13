package io.obolonsky.podcaster.ui

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.data.room.entities.Book
import io.obolonsky.podcaster.databinding.FragmentBookFeedBinding
import io.obolonsky.podcaster.misc.launchWhenStarted
import io.obolonsky.podcaster.ui.adapters.BookFeedPagingAdapter
import io.obolonsky.podcaster.ui.adapters.OffsetItemDecorator
import io.obolonsky.podcaster.viewmodels.SongsViewModel
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class BookFeedFragment : AbsFragment(R.layout.fragment_book_feed) {

    private val songsViewModel: SongsViewModel by viewModels()

    private val binding: FragmentBookFeedBinding by viewBinding()

    override fun initViewModels() {
        songsViewModel.books
            .onEach {
                (binding.recyclerFeed.adapter as? BookFeedPagingAdapter)
                    ?.apply { submitData(lifecycle, it) }
            }
            .launchWhenStarted(lifecycleScope)
    }

    override fun initViews(savedInstanceState: Bundle?) {
        binding.recyclerFeed.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = BookFeedPagingAdapter(onClick = ::onBookClicked)

            addItemDecoration(
                OffsetItemDecorator(resources.getDimensionPixelOffset(R.dimen.big_margin))
            )
        }
        songsViewModel.loadBooks()
    }

    private fun onBookClicked(book: Book) {
        Timber.d("onBookClicked book: ${book.title}")
        val action = BookFeedFragmentDirections
            .actionBookFeedFragmentToBookDetailsFragment(book.id)
        findNavController().navigate(action)
    }
}