package io.obolonsky.player_feature.actions

import android.content.Context
import android.content.Intent
import io.obolonsky.core.di.actions.StopPlayerService
import io.obolonsky.player_feature.player.PodcasterPlaybackService
import javax.inject.Inject

class StopPlayerServiceAction @Inject constructor() : StopPlayerService {

    override fun stop(context: Context) {
        context.stopService(Intent(context, PodcasterPlaybackService::class.java))
    }
}