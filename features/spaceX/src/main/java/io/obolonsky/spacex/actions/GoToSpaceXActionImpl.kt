package io.obolonsky.spacex.actions

import android.content.Context
import android.content.Intent
import io.obolonsky.core.di.actions.GoToSpaceXAction
import io.obolonsky.spacex.ui.SpaceXActivity
import javax.inject.Inject

class GoToSpaceXActionImpl @Inject constructor() : GoToSpaceXAction {

    override fun navigate(context: Context) {
        context.startActivity(Intent(context, SpaceXActivity::class.java))
    }
}