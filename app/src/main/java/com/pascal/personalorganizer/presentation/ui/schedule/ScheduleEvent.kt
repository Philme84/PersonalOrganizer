package com.pascal.personalorganizer.presentation.ui.schedule

import com.pascal.personalorganizer.domain.models.Schedule

sealed class ScheduleEvent {

    object OnNextMonth: ScheduleEvent()
    object OnPrevMonth: ScheduleEvent()
    data class OnDaySelection(val index: Int) : ScheduleEvent()
    object AddNewSchedule: ScheduleEvent()
    data class OnEditSchedule(val schedule: Schedule): ScheduleEvent()
    object OnShowDeleteDialog: ScheduleEvent()
    data class OnDeleteSchedule(val scheduleId: Int): ScheduleEvent()
}