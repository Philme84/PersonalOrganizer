package com.pascal.personalorganizer.data.local.daos

import androidx.room.*
import com.pascal.personalorganizer.data.local.entities.ReminderEntity
import com.pascal.personalorganizer.util.Constants.REMINDER_TABLE

@Dao
interface ReminderDao {

    @Query("SELECT * FROM $REMINDER_TABLE")
    suspend fun getReminders(): List<ReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity)

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Query("DELETE FROM $REMINDER_TABLE WHERE id = :reminderId")
    suspend fun deleteReminder(reminderId: Int)

}