package com.pascal.personalorganizer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pascal.personalorganizer.data.local.daos.ForecastDao
import com.pascal.personalorganizer.data.local.daos.NotesDao
import com.pascal.personalorganizer.data.local.daos.ReminderDao
import com.pascal.personalorganizer.data.local.daos.ScheduleDao
import com.pascal.personalorganizer.data.local.entities.ForecastEntity
import com.pascal.personalorganizer.data.local.entities.NotesEntity
import com.pascal.personalorganizer.data.local.entities.ReminderEntity
import com.pascal.personalorganizer.data.local.entities.ScheduleEntity
import com.pascal.personalorganizer.data.local.typeconverters.DailyTypeConverter
import com.pascal.personalorganizer.data.local.typeconverters.NotesTypeConverter
import com.pascal.personalorganizer.data.local.typeconverters.TodayTypeConverter


@Database(entities = [ForecastEntity::class, ReminderEntity::class, ScheduleEntity::class, NotesEntity::class], version = 1, exportSchema = false)
@TypeConverters(DailyTypeConverter::class, TodayTypeConverter::class, NotesTypeConverter::class)
abstract class ForecastDatabase: RoomDatabase() {

    abstract fun getForecastDao(): ForecastDao
    abstract fun  getReminderDao() : ReminderDao
    abstract fun getScheduleDao(): ScheduleDao
    abstract fun getNotesDao(): NotesDao

}