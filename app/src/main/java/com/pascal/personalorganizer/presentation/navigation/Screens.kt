package com.pascal.personalorganizer.presentation.navigation

import com.pascal.personalorganizer.R

sealed class Screens (val route: String, val title: String, val icon: Int, val fab: Boolean = false) {

    object NotesScreen: Screens("notes_screen_route", "Notes", icon = R.drawable.ic_notes, fab = true)
    object AddNotesScreen: Screens("add_notes_screen_route", "Add new notes", 0 , false)
    object ScheduleScreen: Screens("schedule_screen_route", "Schedule", icon = R.drawable.ic_schedule, fab = true)
    object AddScheduleScreen: Screens("add_schedule_screen_route", "Add new schedule", icon = 0, fab = false)
    object ReminderScreen: Screens("reminder_screen_route", "Reminder", icon = R.drawable.ic_reminders, fab = true)
    object AddReminderScreen: Screens("add_reminder_screen_route", "Add new reminder", icon = 0, fab = false)
    object WeatherScreen: Screens("weather_screen_route", "Weather", icon = R.drawable.ic_weather)

}