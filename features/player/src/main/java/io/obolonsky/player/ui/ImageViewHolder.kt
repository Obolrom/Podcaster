package io.obolonsky.player.ui

import androidx.recyclerview.widget.RecyclerView
import coil.load
import io.obolonsky.player.databinding.MediaImageItemBinding

class ImageViewHolder(
    private val binding: MediaImageItemBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(imageUrl: String) {
        binding.audioImage.load(imageUrl) {
            crossfade(500)
        }
    }
}