package com.pascal.personalorganizer

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.pascal.personalorganizer.util.Constants.CHANNEL_ID
import com.pascal.personalorganizer.util.Constants.INTENT_BODY
import com.pascal.personalorganizer.util.Constants.INTENT_ID

class ReminderReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notification  = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_reminders)
            .setContentTitle("Personal Organizer")
            .setContentText(intent.getStringExtra(INTENT_BODY))
            .build()

        val notificationID = intent.getIntExtra(INTENT_ID, 1)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
    }
}