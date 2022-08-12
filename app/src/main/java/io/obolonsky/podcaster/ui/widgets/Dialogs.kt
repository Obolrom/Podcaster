package io.obolonsky.podcaster.ui.widgets

import android.app.Dialog
import android.content.Context
import timber.log.Timber

abstract class AbsDialogManager {

    protected var dialog: Dialog? = null

    fun destroyDialog() {
        try {
            dialog?.dismiss()
            dialog = null
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}

object LoadingDialog : AbsDialogManager() {

    fun showLoadingDialog(context: Context) {
        if (dialog?.isShowing == true) return
        dialog = LoadingViewDialog(context).also { it.show() }
    }
}