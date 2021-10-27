package io.obolonsky.podcaster

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import io.obolonsky.podcaster.di.AppComponent
import io.obolonsky.podcaster.di.DaggerAppComponent
import timber.log.Timber

class PodcasterApp : Application(), ViewModelStoreOwner, LifecycleObserver {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .application(this)
            .context(this)
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        initTimber()

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun getViewModelStore(): ViewModelStore = ViewModelStore()
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is PodcasterApp -> appComponent
        else -> applicationContext.appComponent
    }