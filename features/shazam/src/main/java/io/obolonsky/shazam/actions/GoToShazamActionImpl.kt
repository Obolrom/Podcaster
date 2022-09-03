package io.obolonsky.shazam.actions

import android.content.Context
import android.content.Intent
import io.obolonsky.core.di.actions.GoToShazamAction
import io.obolonsky.shazam.ui.ShazamActivity
import javax.inject.Inject

class GoToShazamActionImpl @Inject constructor() : GoToShazamAction {

    override fun navigate(context: Context) {
        context.startActivity(Intent(context, ShazamActivity::class.java))
    }
}