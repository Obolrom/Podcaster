package io.obolonsky.podcaster.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.data.responses.MusicItem
import io.obolonsky.podcaster.databinding.MusicItemLayoutBinding

class MusicItemAdapter: BaseAdapter<MusicItem>(MusicItemComparator(), R.layout.music_item_layout) {

    inner class MusicItemViewHolder(private val binding: MusicItemLayoutBinding) :
        BaseAdapter.BaseViewHolder<MusicItem>(binding.root) {

        override fun bind(item: MusicItem) {
            binding.musicItem = item
            binding.executePendingBindings()

            itemView.setOnClickListener {
                onClick?.onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<MusicItem> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = MusicItemLayoutBinding.inflate(layoutInflater, parent, false)

        return MusicItemViewHolder(binding)
    }

    class MusicItemComparator : DiffUtil.ItemCallback<MusicItem>() {
        override fun areItemsTheSame(oldItem: MusicItem, newItem: MusicItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MusicItem, newItem: MusicItem): Boolean {
            return oldItem.id== newItem.id
                    && oldItem.title == newItem.title
        }
    }
}