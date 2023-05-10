package com.pascal.personalorganizer.di

import android.content.Context
import androidx.room.Room
import com.pascal.personalorganizer.data.local.ForecastDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    private const val PERSONAL_ORGANIZER_DATABASE = "personal_organizer_database"

    @Singleton
    @Provides
    fun provideRoom(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            ForecastDatabase::class.java,
            PERSONAL_ORGANIZER_DATABASE
        ).build()

    @Singleton
    @Provides
    fun provideForecastDao(db: ForecastDatabase) = db.getForecastDao()

    @Singleton
    @Provides
    fun provideRemindersDao(db: ForecastDatabase) = db.getReminderDao()

    @Singleton
    @Provides
    fun provideScheduleDao(db: ForecastDatabase) = db.getScheduleDao()

    @Singleton
    @Provides
    fun provideNotesDao(db: ForecastDatabase) = db.getNotesDao()
}