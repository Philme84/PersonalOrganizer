package com.pascal.personalorganizer.data.local.daos

import androidx.room.*
import com.pascal.personalorganizer.data.local.entities.ScheduleEntity
import com.pascal.personalorganizer.util.Constants.SCHEDULE_TABLE

@Dao
interface ScheduleDao {

    @Query("SELECT * FROM $SCHEDULE_TABLE WHERE selectedDay = :selectedDay" )
    suspend fun getSchedules(selectedDay: String): List<ScheduleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ScheduleEntity)

    @Update
    suspend fun updateSchedule(schedule: ScheduleEntity)

    @Query("DELETE FROM $SCHEDULE_TABLE WHERE id = :scheduleId")
    suspend fun deleteSchedule(scheduleId: Int)

}