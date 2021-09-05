package io.obolonsky.podcaster.ui.adapters

import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.obolonsky.podcaster.data.responses.MusicItem

abstract class BaseAdapter<T>(
    diffCallback: DiffUtil.ItemCallback<T>,
    @LayoutRes private val layoutRes: Int,
) : ListAdapter<T, BaseAdapter.BaseViewHolder<T>>(diffCallback) {

    var onClick: OnClickItemListener<T>? = null

    interface OnClickItemListener<T> {
        fun onItemClick(item: T)
    }

    abstract class BaseViewHolder<T>(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}