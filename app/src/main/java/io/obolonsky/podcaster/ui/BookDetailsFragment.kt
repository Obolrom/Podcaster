package io.obolonsky.podcaster.ui

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.data.misc.handle
import io.obolonsky.podcaster.data.room.entities.Book
import io.obolonsky.podcaster.viewmodels.SongsViewModel
import kotlinx.android.synthetic.main.fragment_book_details.*
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.round

@AndroidEntryPoint
class BookDetailsFragment : AbsFragment(R.layout.fragment_book_details) {

    private val songsViewModel: SongsViewModel by activityViewModels()

    override fun initViewModels() {
        songsViewModel.book.handle(this, ::onBook)
        songsViewModel.loadFullBook(228)
    }

    override fun initViews(savedInstanceState: Bundle?) {

    }

    private fun onBook(book: Book) {
        book_banner.load(book.imageUrl) {
            crossfade(500)
        }

        book_title.text = book.title

        book_author.text = book.bookAuthor

        book_rating_bar.rating = book.rating

        book_rating_number.text = String.format("%.1f", book.rating)
    }
}