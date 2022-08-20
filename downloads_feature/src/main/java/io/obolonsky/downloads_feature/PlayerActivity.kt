package io.obolonsky.downloads_feature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.downloads_feature.di.DaggerDownloadsComponent
import javax.inject.Inject

class PlayerActivity : AppCompatActivity() {

    @Inject
    lateinit var player: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerDownloadsComponent.factory()
            .create((applicationContext as App).getAppComponent())
            .inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        findViewById<PlayerView>(R.id.player_view)
            .player = player

        player.setMediaItem(MediaItem.fromUri("https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview123/v4/a9/a7/a3/a9a7a3dc-9c81-9136-4af9-db151768987f/mzaf_2782675308991630591.plus.aac.ep.m4a"))
    }
}