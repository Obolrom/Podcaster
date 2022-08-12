package io.obolonsky.podcaster.ui.adapters

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridItemDecoration(
    private val space: Int,
    private val spanCount: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            top = if (parent.getChildAdapterPosition(view) in 0 until spanCount) 0 else space
            if (parent.getChildAdapterPosition(view) % 2 == 0) {
                left = space / 2
                right = space / 4
            } else {
                left = space / 4
                right = space / 2
            }
            bottom = 0
        }
    }

}