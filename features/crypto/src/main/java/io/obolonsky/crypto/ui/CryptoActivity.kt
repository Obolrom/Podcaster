package io.obolonsky.crypto.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import io.obolonsky.crypto.R

class CryptoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crypto)

        supportFragmentManager.commit {
            replace(R.id.container, CoinFeedFragment())
        }
    }
}