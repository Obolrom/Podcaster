package io.obolonsky.nasa.ui

import androidx.recyclerview.widget.RecyclerView
import coil.load
import io.obolonsky.nasa.databinding.ApodItemBinding

class ImageViewHolder(
    private val binding: ApodItemBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(imageUrl: String) {
        binding.image.load(imageUrl) {
            crossfade(400)
        }
    }
}