package com.pascal.personalorganizer.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pascal.personalorganizer.domain.models.Reminder
import com.pascal.personalorganizer.util.Constants

@Entity(tableName = Constants.REMINDER_TABLE)
class ReminderEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "repeatDaily") val repeatDaily: Boolean,
)


/**
 * Wee need to map our Weather object to a Entity so it can be inserted to our room database.
 * ToEntity without id because it is given by room when we insert to the db.
 * ToEntityForUpdate with id because room needs it to know what to update
 */
fun Reminder.toEntity() = ReminderEntity(
    title = title,
    date = date,
    repeatDaily = repeatDaily
)
fun Reminder.toEntityForUpdate() = ReminderEntity(
    id = id,
    title = title,
    date = date,
    repeatDaily = repeatDaily
)