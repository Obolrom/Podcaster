package io.obolonsky.crypto.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import io.obolonsky.core.di.data.coinpaprika.CoinPaprika
import io.obolonsky.crypto.databinding.CoinFeedItemBinding

internal class CoinFeedAdapter(
    diffUtil: DiffUtil.ItemCallback<CoinPaprika> = DiffUtilCallback(),
    private val onClick: (CoinPaprika) -> Unit,
) : ListAdapter<CoinPaprika, CoinFeedViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinFeedViewHolder {
        val binding = CoinFeedItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CoinFeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoinFeedViewHolder, position: Int) {
        getItem(position)?.let { coin ->
            holder.bind(coin)
            holder.itemView.setOnClickListener {
                onClick(coin)
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<CoinPaprika>() {

        override fun areItemsTheSame(oldItem: CoinPaprika, newItem: CoinPaprika): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CoinPaprika, newItem: CoinPaprika): Boolean {
            return oldItem.name == newItem.name
                    && oldItem.isActive == newItem.isActive
        }
    }
}