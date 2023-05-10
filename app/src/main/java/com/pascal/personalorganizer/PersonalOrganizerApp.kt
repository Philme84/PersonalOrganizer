package com.pascal.personalorganizer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.pascal.personalorganizer.util.Constants.CHANNEL_DESCRIPTION
import com.pascal.personalorganizer.util.Constants.CHANNEL_ID
import com.pascal.personalorganizer.util.Constants.CHANNEL_NAME
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PersonalOrganizerApp: Application() {

    override fun onCreate() {
        super.onCreate()

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = CHANNEL_DESCRIPTION

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)


    }


}