package io.obolonsky.podcaster.ui

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.data.misc.Toaster
import io.obolonsky.podcaster.data.room.StatefulData
import io.obolonsky.podcaster.data.room.entities.Book
import io.obolonsky.podcaster.misc.NetworkBroadcastReceiver
import io.obolonsky.podcaster.viewmodels.SongsViewModel
import kotlinx.android.synthetic.main.fragment_book_details.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class BookDetailsFragment : AbsFragment(R.layout.fragment_book_details) {

    private val songsViewModel: SongsViewModel by activityViewModels()

    private val networkBroadcastReceiver by lazy {
        NetworkBroadcastReceiver(::onNetworkConnectionChanged)
    }

    override fun initViewModels() {
        songsViewModel.book
            .onEach { statefulData ->
                when (statefulData) {
                    is StatefulData.Success -> {
                        statefulData.data?.let { onBook(it) }
                        handleLoading(false)
                    }

                    is StatefulData.Error -> {
                        handleLoading(false)
                        Toaster.showToast(requireContext(), "error")
                    }

                    is StatefulData.Loading -> {
//                        handleLoading(true)
                    }
                }
            }
            .launchIn(lifecycleScope)
    }

    override fun initViews(savedInstanceState: Bundle?) {
        songsViewModel.loadBook(getBookId())

        listen.setOnClickListener {
            findNavController()
                .navigate(R.id.action_bookDetailsFragment_to_newPlayerFragment)
        }
    }

    private fun onBook(book: Book) {
        book_banner.load(book.imageUrl) {
            crossfade(500)
        }

        book_title.text = book.title

        book.bookAuthor?.let { book_author.text = it.fullName }

        book_rating_bar.rating = book.raiting.toFloat()

        book_rating_number.text = String.format("%.1f", book.raiting.toFloat())

        book_rated_by_users.text = getString(R.string.details_rated_by_users, book.auditions)

        book_description.text = book.description

        total_duration.text = getString(
            R.string.book_total_duration,
            book.duration?.div(TimeUnit.MINUTES.toMillis(1))
        )

        book_memory_size.text = "15 MB"
    }

    override fun onStart() {
        super.onStart()
        context?.let { networkBroadcastReceiver.registerReceiver(it) }
    }

    override fun onStop() {
        super.onStop()
        context?.let { networkBroadcastReceiver.unregisterReceiver(it) }
    }

    private fun getBookId() = arguments?.getString("bookId") ?: ""

    private fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            songsViewModel.loadBook(getBookId())
        } else {
            Toaster.showToast(requireContext(), "Network disconnected")
        }
    }
}