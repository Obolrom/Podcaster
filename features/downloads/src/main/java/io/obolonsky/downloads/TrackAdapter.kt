package io.obolonsky.downloads

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import io.obolonsky.core.di.data.Track
import io.obolonsky.downloads.databinding.DownloadTrackItemBinding

class TrackAdapter(
    private val onTrackClick: (Track) -> Unit,
    private val onDownloadTrack: (Track) -> Unit,
) : ListAdapter<Track, TrackViewHolder>(TRACK_DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = DownloadTrackItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrackViewHolder(binding, onDownloadTrack)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        getItem(position)?.let { track ->
            holder.bind(track)
            holder.itemView.setOnClickListener { onTrackClick(track) }
        }
    }

    companion object {

        val TRACK_DIFF_UTIL = object : DiffUtil.ItemCallback<Track>() {
            override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
                return oldItem.audioUri == newItem.audioUri
            }

            override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
                return oldItem.title == newItem.title
                        && oldItem.subtitle == newItem.subtitle
                        && oldItem.downloadStatus == newItem.downloadStatus
            }
        }
    }
}