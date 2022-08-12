package io.obolonsky.podcaster.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import io.obolonsky.podcaster.data.room.entities.Book

class BookSearchAdapter(
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
) : ListAdapter<Book, BookViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        return BookViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        getItem(position)
            ?.let { book ->
                holder.bind(book)
                holder.itemView.setOnClickListener { onClick(book) }
            }
    }
}