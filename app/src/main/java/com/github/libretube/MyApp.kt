package com.github.libretube

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.github.libretube.preferences.PreferenceHelper
import com.github.libretube.util.NotificationHelper
import com.github.libretube.util.NotificationWorker
import java.util.concurrent.TimeUnit


class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        /**
         * initialize the needed [NotificationChannel]s for DownloadService and BackgroundMode
         */
        initializeNotificationChannels()

        /**
         * set the applicationContext as context for the [PreferenceHelper]
         */
        PreferenceHelper.setContext(applicationContext)

        /**
         * bypassing fileUriExposedException, see https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
         */
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        /**
         * initialize the notification listener in the background
         */
        NotificationHelper.enqueueWork(this)
    }

    /**
     * Initializes the required [NotificationChannel]s for the app.
     */
    private fun initializeNotificationChannels() {
        createNotificationChannel(
            "download_service",
            "Download Service",
            "Shows a notification when downloading media.",
            NotificationManager.IMPORTANCE_NONE
        )
        createNotificationChannel(
            "background_mode",
            "Background Mode",
            "Shows a notification with buttons to control the audio player",
            NotificationManager.IMPORTANCE_LOW
        )
        createNotificationChannel(
            "notification_worker",
            "Notification Worker",
            "Shows a notification when new streams are available.",
            NotificationManager.IMPORTANCE_DEFAULT
        )
    }

    private fun createNotificationChannel(
        id: String,
        name: String,
        descriptionText: String,
        importance: Int
    ) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(id, name, importance)
            channel.description = descriptionText
            // Register the channel in the system
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
