package io.obolonsky.podcaster.ui.adapters

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class OffsetItemDecorator(
    private val offset: Int = 0,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        with(outRect) {
            top = if (position == 0) offset / 2 else 0
            left = offset / 2
            right = offset / 2
            bottom = offset / 2
        }
    }
}