package com.pascal.personalorganizer.presentation.ui.reminders.add

data class AddReminderUIState (
    val title : String = "",
    val titleError: Boolean = false,
    val date: String = "Date",
    val dailyCheck : Boolean = false,
)