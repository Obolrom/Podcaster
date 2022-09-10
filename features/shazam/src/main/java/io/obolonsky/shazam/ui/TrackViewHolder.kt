package io.obolonsky.shazam.ui

import androidx.recyclerview.widget.RecyclerView
import coil.load
import io.obolonsky.core.di.data.Track
import io.obolonsky.shazam.databinding.TrackItemBinding

class TrackViewHolder(
    private val binding: TrackItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        track.imageUrls.firstOrNull()?.let { imageUrl ->
            binding.image.load(imageUrl) {
                crossfade(500)
            }
        }

        track.title?.let { binding.title.text = it }
        track.subtitle?.let { binding.subtitle.text = it }
    }
}