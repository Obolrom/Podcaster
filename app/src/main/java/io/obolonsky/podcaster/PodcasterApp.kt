package io.obolonsky.podcaster

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import io.obolonsky.podcaster.di.AppComponent
import io.obolonsky.podcaster.di.injectors.AppInjector
import timber.log.Timber

class PodcasterApp : Application(), ViewModelStoreOwner, LifecycleObserver {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        AppInjector.init(this)

        appComponent = AppInjector.appComponent

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