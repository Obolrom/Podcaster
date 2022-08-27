package io.obolonsky.podcaster

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.core.di.depsproviders.ApplicationProvider
import io.obolonsky.podcaster.background.PodcasterWorkerFactory
import io.obolonsky.podcaster.di.components.AppComponent
import timber.log.Timber
import javax.inject.Inject

class PodcasterApp : Application(), App {

    @Inject
    lateinit var workerFactory: PodcasterWorkerFactory

    val appComponent by lazy {
        AppComponent.create(this)
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

    override fun getAppComponent(): ApplicationProvider = appComponent

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}