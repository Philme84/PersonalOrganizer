package com.pascal.personalorganizer.data.repositoryImpl

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.pascal.personalorganizer.ReminderReceiver
import com.pascal.personalorganizer.data.local.daos.ScheduleDao
import com.pascal.personalorganizer.data.local.entities.toEntity
import com.pascal.personalorganizer.data.local.entities.toEntityForUpdate
import com.pascal.personalorganizer.domain.models.Schedule
import com.pascal.personalorganizer.domain.models.toDomain
import com.pascal.personalorganizer.domain.repository.ScheduleRepository
import com.pascal.personalorganizer.util.Constants.INTENT_BODY
import com.pascal.personalorganizer.util.Constants.INTENT_ID
import javax.inject.Inject

class ScheduleRepositoryImpl  @Inject constructor(
    private val context: Context,
    private val dao: ScheduleDao
): ScheduleRepository {

    override suspend fun getAllSchedules(selectedDay: String): List<Schedule> {
        val schedules = dao.getSchedules(selectedDay)
        return schedules.map {//We are getting a entity from our db, so wee need to map it to a object.
            it.toDomain()
        }
    }

    override suspend fun insertSchedule(schedule: Schedule) {
        dao.insertSchedule(schedule.toEntity())// We need to convert our object to a entity so it can be inserted to the db
    }

    override suspend fun updateSchedule(schedule: Schedule) {
        dao.updateSchedule(schedule.toEntityForUpdate())
    }

    override suspend fun deleteSchedule(scheduleId: Int) {
        dao.deleteSchedule(scheduleId)
    }

    override fun scheduleSchedule(id: Int, text: String, epoch: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        intent.putExtra(INTENT_ID, id)
        intent.putExtra(INTENT_BODY, text)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val reminderTime = epoch*1000L

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminderTime,
            pendingIntent
        )

    }

    override fun cancelSchedule(id: Int) {
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