package com.pascal.personalorganizer.di

import android.content.Context
import com.pascal.personalorganizer.data.local.ForecastDatabase
import com.pascal.personalorganizer.data.remote.WeatherApiService
import com.pascal.personalorganizer.data.repositoryImpl.*
import com.pascal.personalorganizer.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideWeatherRepository(
        @ApplicationContext context: Context,
        apiService: WeatherApiService,
        db: ForecastDatabase
    ): WeatherRepository{
        return  WeatherRepositoryImpl(
            context,
            apiService,
            db.getForecastDao()
        )
    }

    @Provides
    @Singleton
    fun provideReminderRepository(
        @ApplicationContext context: Context,
        db: ForecastDatabase
    ): ReminderRepository{
        return ReminderRepositoryImpl(
            context,
            db.getReminderDao()
        )
    }

    @Provides
    @Singleton
    fun provideScheduleRepository(
        @ApplicationContext context: Context,
        db: ForecastDatabase
    ): ScheduleRepository{
        return ScheduleRepositoryImpl(
            context,
            db.getScheduleDao()
        )
    }

    @Provides
    @Singleton
    fun providesRecorderRepository(
        @ApplicationContext context: Context,
    ): RecorderRepository{
        return RecorderRepositoryImpl(
            context
        )
    }

    @Provides
    @Singleton
    fun provideNotesRepository(
        db: ForecastDatabase
    ): NotesRepository{
        return NotesRepositoryImpl(
            db.getNotesDao()
        )
    }

}