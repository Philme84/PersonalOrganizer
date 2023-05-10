package com.pascal.personalorganizer.data.repositoryImpl

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.pascal.personalorganizer.ReminderReceiver
import com.pascal.personalorganizer.data.local.daos.ReminderDao
import com.pascal.personalorganizer.data.local.entities.toEntity
import com.pascal.personalorganizer.data.local.entities.toEntityForUpdate
import com.pascal.personalorganizer.domain.models.Reminder
import com.pascal.personalorganizer.domain.models.toDomain
import com.pascal.personalorganizer.domain.repository.ReminderRepository
import com.pascal.personalorganizer.util.Constants.INTENT_BODY
import com.pascal.personalorganizer.util.Constants.INTENT_ID
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val context: Context,
    private val dao: ReminderDao
): ReminderRepository {


    override suspend fun getAllReminders(): List<Reminder> {
        val reminders = dao.getReminders()
        return reminders.map {//We are getting a entity from our db, so wee need to map it to a object.
            it.toDomain()
        }
    }

    override suspend fun insertNewReminder(reminder: Reminder) {
        dao.insertReminder(reminder.toEntity()) // We need to convert our object to a entity so it can be inserted to the db
    }

    override suspend fun updateReminder(reminder: Reminder) {
        dao.updateReminder(reminder.toEntityForUpdate())
    }

    override suspend fun deleteReminder(reminderId: Int) {
        dao.deleteReminder(reminderId = reminderId)
    }

    override fun scheduleReminder(id: Int, text: String, epoch: Long, isDaily: Boolean) {
        //To trigger a reminder at a specific time, either daily or just once.

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        intent.putExtra(INTENT_ID, id) // we add the id to get it the reminder receiver
        intent.putExtra(INTENT_BODY, text) // we add the text to get it the reminder receiver

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val repeatInterval = AlarmManager.INTERVAL_DAY // To repeat daily
        val reminderTime = epoch*1000L // We get the date in epoch, but the alarm manager needs it in milliseconds.

        if (isDaily){
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                reminderTime,
                repeatInterval,
                pendingIntent
            )
        }else{
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminderTime,
                pendingIntent
            )
        }
    }

    override fun cancelReminder(id: Int) {
        //to cancel a reminder using its id.
        val intent = Intent(context, ReminderReceiver::class.java )
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}