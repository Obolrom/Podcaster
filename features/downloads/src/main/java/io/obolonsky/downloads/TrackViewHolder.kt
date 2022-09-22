package io.obolonsky.downloads

import androidx.recyclerview.widget.RecyclerView
import coil.load
import io.obolonsky.core.di.DownloadStatus.*
import io.obolonsky.core.di.data.Track
import io.obolonsky.downloads.databinding.DownloadTrackItemBinding

class TrackViewHolder(
    private val binding: DownloadTrackItemBinding,
    private val onRemoveTrack: (Track) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        when (track.downloadStatus) {
            QUEUED, COMPLETED -> {
                binding.removeRecentTrack.load(R.drawable.ic_round_download_done_24)
                binding.removeRecentTrack.setOnClickListener(null)
            }
            STOPPED, DOWNLOADING, FAILED, NOT_DOWNLOADED -> {
                binding.removeRecentTrack.load(R.drawable.ic_round_download_24)
                binding.removeRecentTrack.setOnClickListener {
                    onRemoveTrack(track)
                }
            }
            else -> { }
        }
        track.imageUrls.firstOrNull()?.let { imageUrl ->
            binding.image.load(imageUrl) {
                crossfade(500)
            }
        }

        track.title?.let { binding.title.text = it }
        track.subtitle?.let { binding.subtitle.text = it }
    }

    fun updateDownloadStatus(track: Track) {
        when (track.downloadStatus) {
            QUEUED, COMPLETED -> {
                binding.removeRecentTrack.load(R.drawable.ic_round_download_done_24)
                binding.removeRecentTrack.setOnClickListener(null)
            }
            STOPPED, DOWNLOADING, FAILED, NOT_DOWNLOADED -> {
                binding.removeRecentTrack.load(R.drawable.ic_round_download_24)
                binding.removeRecentTrack.setOnClickListener {
                    onRemoveTrack(track)
                }
            }
            else -> { }
        }
    }
}