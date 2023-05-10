package com.pascal.personalorganizer.presentation.ui.schedule.add

data class AddScheduleUIState (
    val title : String = "",
    val titleError: Boolean = false,
    val hour: String = "Hour",
    val reminderCheck : Boolean = false,
)