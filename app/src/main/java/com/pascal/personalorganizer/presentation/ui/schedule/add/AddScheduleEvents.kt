package com.pascal.personalorganizer.presentation.ui.schedule.add


import com.pascal.personalorganizer.domain.models.Schedule

sealed class AddScheduleEvents {
    object AddNewSchedule: AddScheduleEvents()
    data class OnTitleChange(val newValue: String): AddScheduleEvents()
    data class OnTimePicker(val value: Long): AddScheduleEvents()
    data class OnReminderCheckedChange(val newValue: Boolean): AddScheduleEvents()
    object OnBackButton: AddScheduleEvents()
    data class SetExistingInfo(val schedule: Schedule): AddScheduleEvents()
}