package io.obolonsky.player.player

import androidx.media3.session.CommandButton
import androidx.media3.session.SessionCommand
import io.obolonsky.coreui.R as CoreUiR

fun getForwardCommandButton(sessionCommand: SessionCommand): CommandButton {
    return CommandButton.Builder()
        .setSessionCommand(sessionCommand)
        .setEnabled(true)
        .setIconResId(CoreUiR.drawable.ic_round_forward_30)
        .build()
}

fun getRewindCommandButton(sessionCommand: SessionCommand): CommandButton {
    return CommandButton.Builder()
        .setSessionCommand(sessionCommand)
        .setEnabled(true)
        .setIconResId(CoreUiR.drawable.ic_round_replay_30)
        .build()
}