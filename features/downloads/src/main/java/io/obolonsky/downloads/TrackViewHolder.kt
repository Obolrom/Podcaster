package io.obolonsky.downloads

import androidx.recyclerview.widget.RecyclerView
import coil.load
import io.obolonsky.core.di.data.Track
import io.obolonsky.downloads.databinding.DownloadTrackItemBinding

class TrackViewHolder(
    private val binding: DownloadTrackItemBinding,
    private val onRemoveTrack: (Track) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        binding.removeRecentTrack.setOnClickListener {
            onRemoveTrack(track)
        }
        track.imageUrls.firstOrNull()?.let { imageUrl ->
            binding.image.load(imageUrl) {
                crossfade(500)
            }
        }

        track.title?.let { binding.title.text = it }
        track.subtitle?.let { binding.subtitle.text = it }
    }
}