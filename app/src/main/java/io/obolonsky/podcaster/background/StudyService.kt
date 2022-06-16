package io.obolonsky.podcaster.background

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Rect
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_LOW
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.ui.MainActivity
import io.obolonsky.podcaster.ui.MainActivity_GeneratedInjector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@RequiresApi(Build.VERSION_CODES.O)
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
            delay(5_000L)
            stopForeground(true)
            delay(30_000L)
            Timber.d("studyService stopSelf")
            withContext(Dispatchers.Main) {
                stopSelf()
            }
        }
        super.onCreate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val channel = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
        return channelId
    }

    private val pendingIntent by lazy {
        Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    private val notification by lazy {
        NotificationCompat.Builder(this, createNotificationChannel("fuck", "you"))
            .setContentTitle("Study")
            .setContentText("Fuck you")
            .setTicker("Hey")
            .setSmallIcon(R.drawable.exo_icon_next)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("studyService onStartCommand")
        Timber.d("studyService startForeground")
        startForeground(1488, notification)

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