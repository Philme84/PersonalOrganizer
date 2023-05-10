package com.pascal.personalorganizer.domain.models

import com.pascal.personalorganizer.data.local.entities.ReminderEntity


data class Reminder(
    val id: Int,
    val title: String,
    val date: Long,
    val repeatDaily: Boolean,
)

/**
 * Every time we retrieve data from room database it will return a entity,
 * so we need to map the entity to our Reminder object that is the one being
 * use by our presentation layer
 */
fun ReminderEntity.toDomain() = Reminder(
    id = id,
    title = title,
    date = date,
    repeatDaily = repeatDaily
)