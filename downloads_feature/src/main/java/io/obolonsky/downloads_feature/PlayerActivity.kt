package io.obolonsky.downloads_feature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.downloads_feature.di.DaggerDownloadsComponent

class PlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerDownloadsComponent.factory()
            .create((applicationContext as App).getAppComponent())
            .inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
    }
}