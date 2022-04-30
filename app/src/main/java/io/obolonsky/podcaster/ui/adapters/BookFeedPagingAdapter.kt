package io.obolonsky.podcaster.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.data.room.entities.Book
import kotlinx.android.synthetic.main.book_item.view.*

class BookFeedPagingAdapter(
    diffUtil: DiffUtil.ItemCallback<Book> = object : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.title == newItem.title
                    && oldItem.imageUrl == newItem.imageUrl
        }
    },
) : PagingDataAdapter<Book, BookViewHolder>(diffUtil) {

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        getItem(position)
            ?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        return BookViewHolder(LayoutInflater.from(parent.context), parent)
    }
}

class BookViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup,
) : RecyclerView.ViewHolder(
    inflater.inflate(R.layout.book_item, parent, false)
) {

    fun bind(item: Book) {
        itemView.apply {
            banner.load(item.imageUrl) {
                crossfade(500)
            }
            title.text = item.title
        }
    }
}