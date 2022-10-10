package io.obolonsky.crypto.ui

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.obolonsky.core.di.data.coinpaprika.CoinPaprika
import io.obolonsky.coreui.R as CoreUiR
import io.obolonsky.crypto.databinding.CoinFeedItemBinding

class CoinFeedViewHolder(
    private val coinFeedItem: CoinFeedItemBinding,
) : RecyclerView.ViewHolder(coinFeedItem.root) {

    fun bind(item: CoinPaprika) = coinFeedItem.apply {
        title.text = item.name
        val statusColor =
            if (item.isActive) CoreUiR.color.green
            else CoreUiR.color.red

        status.setBackgroundColor(ContextCompat.getColor(itemView.context, statusColor))
    }
}