package io.obolonsky.podcaster.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.data.room.entities.Song
import io.obolonsky.podcaster.databinding.MusicItemLayoutBinding

class SongAdapter: BaseAdapter<Song>(SongComparator(), R.layout.music_item_layout) {

    inner class SongViewHolder(private val binding: MusicItemLayoutBinding) :
        BaseAdapter.BaseViewHolder<Song>(binding.root) {

        override fun bind(item: Song) {
            binding.musicItem = item
            binding.executePendingBindings()

            itemView.setOnClickListener {
                onClick?.onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Song> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = MusicItemLayoutBinding.inflate(layoutInflater, parent, false)

        return SongViewHolder(binding)
    }

    class SongComparator : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id== newItem.id
                    && oldItem.title == newItem.title
        }
    }
}