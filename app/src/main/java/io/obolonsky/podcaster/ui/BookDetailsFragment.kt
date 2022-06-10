package io.obolonsky.podcaster.ui

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.data.misc.toaster
import io.obolonsky.podcaster.data.room.StatefulData
import io.obolonsky.podcaster.data.room.entities.Book
import io.obolonsky.podcaster.databinding.FragmentBookDetailsBinding
import io.obolonsky.podcaster.misc.NetworkBroadcastReceiver
import io.obolonsky.podcaster.viewmodels.SongsViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class BookDetailsFragment : AbsFragment(R.layout.fragment_book_details) {

    private val songsViewModel: SongsViewModel by activityViewModels()

    private val binding: FragmentBookDetailsBinding by viewBinding()

    private val toaster by toaster()

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
                        toaster.showToast(requireContext(), "error")
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

        binding.listen.setOnClickListener {
            findNavController()
                .navigate(R.id.action_bookDetailsFragment_to_newPlayerFragment)
        }
    }

    private fun onBook(book: Book) {
        binding.bookBanner.load(book.imageUrl) {
            crossfade(500)
        }

        binding.bookTitle.text = book.title

        book.bookAuthor?.let { binding.bookAuthor.text = it.fullName }

        binding.bookRatingBar.rating = book.raiting.toFloat()

        binding.bookRatingNumber.text = String.format("%.1f", book.raiting.toFloat())

        binding.bookRatedByUsers.text = getString(R.string.details_rated_by_users, book.auditions)

        binding.bookDescription.text = book.description

        binding.totalDuration.text = getString(
            R.string.book_total_duration,
            book.duration?.div(TimeUnit.MINUTES.toMillis(1))
        )

        binding.bookMemorySize.text = "15 MB"
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
            toaster.showToast(requireContext(), "Network disconnected")
        }
    }
}