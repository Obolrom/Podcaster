package io.obolonsky.podcaster.ui.widgets

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import io.obolonsky.podcaster.R

class LoadingViewDialog(
    context: Context
) : Dialog(context, R.style.Theme_PodcasterTheme_FullscreenDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.loading_dialog_layout)
    }
}