package com.pascal.personalorganizer.presentation.ui.reminders

import com.pascal.personalorganizer.domain.models.Reminder

sealed class ReminderEvents {

    object AddNewReminder: ReminderEvents()
    data class OnEditReminder(val reminder: Reminder): ReminderEvents()
    object OnShowDeleteDialog: ReminderEvents()
    data class OnDeleteReminder(val reminderId: Int): ReminderEvents()

}