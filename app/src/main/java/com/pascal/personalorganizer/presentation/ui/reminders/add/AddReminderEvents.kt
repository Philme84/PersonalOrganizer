package com.pascal.personalorganizer.presentation.ui.reminders.add

import com.pascal.personalorganizer.domain.models.Reminder


sealed class AddReminderEvents {
    object AddNewReminder: AddReminderEvents()
    data class OnTitleChange(val newValue: String): AddReminderEvents()
    data class OnDailyCheckedChange(val newValue: Boolean): AddReminderEvents()
    data class OnDatePicker(val value: Long): AddReminderEvents()
    object OnBackButton: AddReminderEvents()
    data class SetExistingInfo(val reminder: Reminder): AddReminderEvents()
}