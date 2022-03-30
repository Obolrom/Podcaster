package io.obolonsky.podcaster.ui.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat
import io.obolonsky.podcaster.R
import kotlinx.android.synthetic.main.speed_item_layout.view.*

class SpeedView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.speed_item_layout, this)

        orientation = HORIZONTAL
        setPadding(24, 16, 24, 16)

        context.obtainStyledAttributes(attrs, R.styleable.SpeedView).let {
            setSpeedText(it.getText(R.styleable.SpeedView_speed_text))

            it.recycle()
        }
    }

    fun setSpeedText(text: String?) {
        text?.let { speed_text.text = it }
    }

    fun setSpeedText(charSequence: CharSequence?) {
        charSequence?.let { speed_text.text = it }
    }
}