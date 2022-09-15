package io.obolonsky.nasa.actions

import android.content.Context
import android.content.Intent
import io.obolonsky.core.di.actions.GoToNasaAction
import io.obolonsky.nasa.ui.NasaActivity
import javax.inject.Inject

class GoToNasaActionImpl @Inject constructor() : GoToNasaAction {

    override fun navigate(context: Context) {
        context.startActivity(Intent(context, NasaActivity::class.java))
    }
}