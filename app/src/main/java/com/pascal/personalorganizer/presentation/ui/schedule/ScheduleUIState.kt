package com.pascal.personalorganizer.presentation.ui.schedule

data class ScheduleUIState (
    val currentMonth: String = "",
    val currentDayIndex: Int = 0,
    val prevSelectedIndex: Int = 0,
    val calendarSettingIndicator : Boolean = false,
    val showDialog : Boolean = false,
    val isReady: Boolean = false
)