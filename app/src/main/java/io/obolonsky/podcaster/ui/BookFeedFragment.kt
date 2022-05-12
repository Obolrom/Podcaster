package io.obolonsky.podcaster.ui

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.data.room.entities.Book
import io.obolonsky.podcaster.databinding.FragmentBookFeedBinding
import io.obolonsky.podcaster.ui.adapters.BookFeedPagingAdapter
import io.obolonsky.podcaster.ui.adapters.OffsetItemDecorator
import io.obolonsky.podcaster.viewmodels.SongsViewModel
import timber.log.Timber

@AndroidEntryPoint
class BookFeedFragment : AbsFragment(R.layout.fragment_book_feed) {

    private val songsViewModel: SongsViewModel by viewModels()

    private val binding: FragmentBookFeedBinding by viewBinding()

    override fun initViewModels() {
        songsViewModel.books.observe(this) {
            (binding.recyclerFeed.adapter as? BookFeedPagingAdapter)
                ?.apply { submitData(lifecycle, it) }
        }
    }

    override fun initViews(savedInstanceState: Bundle?) {
        binding.recyclerFeed.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = BookFeedPagingAdapter().apply {
                onClick = ::onBookClicked
            }

            addItemDecoration(
                OffsetItemDecorator(resources.getDimensionPixelOffset(R.dimen.big_margin))
            )
        }
        songsViewModel.loadBooks()
    }

    private fun onBookClicked(book: Book) {
        Timber.d("onBookClicked book: ${book.title}")
        findNavController()
            .navigate(
                resId = R.id.action_bookFeedFragment_to_bookDetailsFragment,
                args = bundleOf(
                    "bookId" to book.id
                )
            )
    }
}