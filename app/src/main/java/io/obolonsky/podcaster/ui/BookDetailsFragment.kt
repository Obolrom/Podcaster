package io.obolonsky.podcaster.ui

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import io.obolonsky.podcaster.R
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
                        Toast.makeText(
                            requireContext(),
                            "error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is StatefulData.Loading -> {
                        handleLoading(true)
                    }
                }
            }
            .launchIn(lifecycleScope)
    }

    override fun initViews(savedInstanceState: Bundle?) {
        songsViewModel.loadBook("4c652d97-ab4a-4897-85f1-1257a2e59200")
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

    private fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            songsViewModel.loadBook("4c652d97-ab4a-4897-85f1-1257a2e59200")
        } else {
            Toast.makeText(
                requireContext(),
                "Network disconnected",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}