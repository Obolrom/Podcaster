package io.obolonsky.podcaster.ui.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.databinding.SpeedItemLayoutBinding

class SpeedView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private val binding: SpeedItemLayoutBinding by viewBinding(createMethod = CreateMethod.INFLATE)

    init {
        orientation = HORIZONTAL
        setPadding(24, 16, 24, 16)

        context.obtainStyledAttributes(attrs, R.styleable.SpeedView).let {
            setSpeedText(it.getText(R.styleable.SpeedView_speed_text))

            it.recycle()
        }
    }

    fun setSpeedText(text: String?) {
        text?.let { binding.speedText.text = it }
    }

    fun setSpeedText(charSequence: CharSequence?) {
        charSequence?.let { binding.speedText.text = it }
    }
}