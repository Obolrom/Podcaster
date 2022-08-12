package io.obolonsky.podcaster.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.data.room.entities.Book

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
    private val onClick: (Book) -> Unit,
) : PagingDataAdapter<Book, BookViewHolder>(diffUtil) {

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        getItem(position)
            ?.let { book ->
                holder.bind(book)
                holder.itemView.setOnClickListener { onClick(book) }
            }
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
            findViewById<AppCompatImageView>(R.id.banner).load(item.imageUrl) {
                crossfade(500)
            }
            findViewById<TextView>(R.id.title).text = item.title
        }
    }
}