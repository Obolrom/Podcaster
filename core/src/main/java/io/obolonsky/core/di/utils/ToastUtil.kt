package io.obolonsky.core.di.utils

import android.content.Context

interface ToastUtil {

    fun showToast(context: Context, message: String)

    fun clear()
}