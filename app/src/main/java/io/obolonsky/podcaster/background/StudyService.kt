package io.obolonsky.podcaster.background

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@AndroidEntryPoint
class StudyService : LifecycleService() {

    override fun onConfigurationChanged(newConfig: Configuration) {
        Timber.d("studyService onConfigurationChanged")
        super.onConfigurationChanged(newConfig)
    }

    override fun onLowMemory() {
        Timber.d("studyService onLowMemory")
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        Timber.d("studyService onTrimMemory")
        super.onTrimMemory(level)
    }

    override fun onCreate() {
        Timber.d("studyService onCreate")
        lifecycleScope.launch(Dispatchers.Main) {
            delay(15_000L)
            Timber.d("studyService stopSelf")
            withContext(Dispatchers.Main) {
                stopSelf()
            }
        }
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("studyService onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Timber.d("studyService onDestroy")
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder {
        Timber.d("studyService onBind")
        super.onBind(intent)
        return StudyBinder(Rect())
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Timber.d("studyService onUnbind")
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        Timber.d("studyService onRebind")
        super.onRebind(intent)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Timber.d("studyService onTaskRemoved")
        super.onTaskRemoved(rootIntent)
    }
}

class StudyBinder(
    private val rect: Rect,
) : Binder() {

    fun test() = rect
}