package io.obolonsky.podcaster.ui

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.ui.adapters.BookFeedPagingAdapter
import io.obolonsky.podcaster.ui.adapters.OffsetItemDecorator
import io.obolonsky.podcaster.viewmodels.SongsViewModel
import kotlinx.android.synthetic.main.fragment_book_feed.*

@AndroidEntryPoint
class BookFeedFragment : AbsFragment(R.layout.fragment_book_feed) {

    private val songsViewModel: SongsViewModel by viewModels()

    override fun initViewModels() {
        songsViewModel.books.observe(this) {
            (recycler_feed.adapter as? BookFeedPagingAdapter)
                ?.apply { submitData(lifecycle, it) }
        }
    }

    override fun initViews(savedInstanceState: Bundle?) {
        recycler_feed.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = BookFeedPagingAdapter()

            addItemDecoration(
                OffsetItemDecorator(resources.getDimensionPixelOffset(R.dimen.big_margin))
            )
        }
        songsViewModel.loadBooks()
    }
}