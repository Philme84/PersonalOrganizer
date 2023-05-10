package com.pascal.personalorganizer.domain.repository

import com.pascal.personalorganizer.domain.models.Reminder

interface ReminderRepository {

    suspend fun getAllReminders(): List<Reminder>
    suspend fun insertNewReminder(reminder: Reminder)
    suspend fun updateReminder(reminder: Reminder)
    suspend fun deleteReminder(reminderId: Int)

    fun scheduleReminder(id: Int, text:String, epoch: Long, isDaily: Boolean)
    fun cancelReminder(id: Int)

}