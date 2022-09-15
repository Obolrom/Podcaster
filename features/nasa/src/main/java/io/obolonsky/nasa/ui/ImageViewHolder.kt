package io.obolonsky.nasa.ui

import android.graphics.drawable.BitmapDrawable
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.ImageRequest
import com.davemorrissey.labs.subscaleview.ImageSource
import io.obolonsky.nasa.databinding.ApodItemBinding

class ImageViewHolder(
    private val binding: ApodItemBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(imageUrl: String) {
        val loader = ImageLoader(itemView.context)
        val req = ImageRequest.Builder(itemView.context)
            .data(imageUrl)
            .target { result ->
                val bitmap = (result as BitmapDrawable).bitmap
                binding.image.setImage(ImageSource.bitmap(bitmap))
            }
            .build()

        val disposable = loader.enqueue(req)
    }
}