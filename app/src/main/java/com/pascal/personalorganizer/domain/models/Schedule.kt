package com.pascal.personalorganizer.domain.models

import com.pascal.personalorganizer.data.local.entities.ScheduleEntity

data class Schedule (
    val id: Int,
    val title: String,
    val selectedDay: String,
    val date: Long,
    val reminder: Boolean,
)

/**
 * Every time we retrieve data from room database it will return a entity,
 * so we need to map the entity to our Schedule object that is the one being
 * use by our presentation layer
 */
fun ScheduleEntity.toDomain() = Schedule(
    id = id,
    title = title,
    selectedDay = selectedDay,
    date = date,
    reminder = reminder
)