package io.obolonsky.podcaster

import android.app.Application
import io.obolonsky.podcaster.di.components.DaggerAppComponent
import timber.log.Timber

class PodcasterApp : Application()/*, Configuration.Provider*/ {

    val appComponent by lazy {
        DaggerAppComponent.builder()
            .context(this)
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        initTimber()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}