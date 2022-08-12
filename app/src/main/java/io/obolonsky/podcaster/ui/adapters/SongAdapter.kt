package io.obolonsky.podcaster.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.alpha
import androidx.recyclerview.widget.DiffUtil
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.data.room.entities.Song
import io.obolonsky.podcaster.databinding.MusicItemLayoutBinding
import timber.log.Timber

class SongAdapter: BaseAdapter<Song>(SongComparator(), R.layout.music_item_layout) {

    inner class SongViewHolder(private val binding: MusicItemLayoutBinding) :
        BaseAdapter.BaseViewHolder<Song>(binding.root) {

        fun updateFavorite(isFavorite: Boolean) {
            binding.name.setTextColor(
                if (isFavorite) Color.GREEN
                else Color.GRAY
            )
        }

        override fun bind(item: Song) {
            binding.name.text = item.title
            updateFavorite(item.isFavorite)

            itemView.setOnClickListener {
//                itemView.setBackgroundColor(itemView.resources.getColor(R.color.design_default_color_background))
                onClick?.onItemClick(item)
            }
        }
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<Song>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNullOrEmpty()) {
            Timber.d("fuckingfuck onBindViewHolder")
            super.onBindViewHolder(holder, position, payloads)
            return
        }
        holder as SongViewHolder
        holder.updateFavorite(currentList[position].isFavorite)
        Timber.d("fuckingfuck onBindViewHolder with payload")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = MusicItemLayoutBinding.inflate(layoutInflater, parent, false)

        return SongViewHolder(binding)
    }

    class SongComparator : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.title == newItem.title
                    && oldItem.isFavorite == newItem.isFavorite
        }

        override fun getChangePayload(oldItem: Song, newItem: Song): Any? {
            return if (oldItem.isFavorite != newItem.isFavorite) true else null
        }
    }
}