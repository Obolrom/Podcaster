package io.obolonsky.podcaster.ui

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.filter
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.data.room.entities.Book
import io.obolonsky.podcaster.databinding.FragmentLibraryBinding
import io.obolonsky.podcaster.misc.launchWhenStarted
import io.obolonsky.podcaster.ui.adapters.BookFeedPagingAdapter
import io.obolonsky.podcaster.ui.adapters.OffsetItemDecorator
import io.obolonsky.podcaster.viewmodels.SongsViewModel
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class LibraryFragment : AbsFragment(R.layout.fragment_library) {

    private val binding by viewBinding<FragmentLibraryBinding>()

    private val songsViewModel by viewModels<SongsViewModel>()

    override fun initViewModels() {
        songsViewModel.books
            .onEach {
                (binding.recyclerFeed.adapter as? BookFeedPagingAdapter)
                    ?.apply { submitData(lifecycle, it.filter {
                        it.title.contains("cal", true) or it.title.contains("prou", true)
                    }) }
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