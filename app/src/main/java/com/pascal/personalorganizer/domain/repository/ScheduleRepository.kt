package com.pascal.personalorganizer.domain.repository

import com.pascal.personalorganizer.domain.models.Schedule

interface ScheduleRepository {

    suspend fun getAllSchedules(selectedDay: String): List<Schedule>
    suspend fun insertSchedule(schedule: Schedule)
    suspend fun updateSchedule(schedule: Schedule)
    suspend fun deleteSchedule(scheduleId: Int)

    fun scheduleSchedule(id: Int, text:String, epoch: Long)
    fun cancelSchedule(id: Int)

}