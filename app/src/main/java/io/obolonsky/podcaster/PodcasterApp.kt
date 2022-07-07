package io.obolonsky.podcaster

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import io.obolonsky.podcaster.background.PodcasterWorkerFactory
import io.obolonsky.podcaster.di.components.DaggerAppComponent
import timber.log.Timber
import javax.inject.Inject

class PodcasterApp : Application() {

    @Inject
    lateinit var workerFactory: PodcasterWorkerFactory

    val appComponent by lazy {
        DaggerAppComponent.builder()
            .context(this)
            .build()
    }

    private val workManagerConfiguration by lazy {
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)

        WorkManager.initialize(this, workManagerConfiguration)

        initTimber()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}