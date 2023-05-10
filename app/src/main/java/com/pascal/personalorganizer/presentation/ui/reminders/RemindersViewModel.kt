package com.pascal.personalorganizer.presentation.ui.reminders

import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pascal.personalorganizer.domain.models.Reminder
import com.pascal.personalorganizer.domain.repository.ReminderRepository
import com.pascal.personalorganizer.presentation.navigation.Screens
import com.pascal.personalorganizer.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val repository: ReminderRepository
) : ViewModel() {

    private val _uiEvent =  Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var reminders = mutableStateListOf<Reminder>()
        private set

    var isReady by mutableStateOf(false)
        private set

    var showDialog by mutableStateOf(false)
        private set

    fun onEvent(event: ReminderEvents){
        when (event){
            is ReminderEvents.AddNewReminder -> {
                sendUiEvent(UiEvent.Navigate(Screens.AddReminderScreen.route + "/-1"))
            }
            is ReminderEvents.OnEditReminder -> {
                sendUiEvent(UiEvent.Navigate(Screens.AddReminderScreen.route + "/${event.reminder.id}?title=${event.reminder.title}&date=${event.reminder.date}&repeatDaily=${event.reminder.repeatDaily}"))
            }
            is ReminderEvents.OnShowDeleteDialog -> {
                showDialog = !showDialog
            }
            is ReminderEvents.OnDeleteReminder -> {
                deleteReminder(event.reminderId)
            }
        }
    }


    fun getReminders(){
        viewModelScope.launch {
            reminders = repository.getAllReminders().toMutableStateList()
            reminders.sortedBy { it.date }
            isReady = true
        }
    }

    private fun deleteReminder(reminderId: Int){
        showDialog = !showDialog
        viewModelScope.launch {
            val index = reminders.indexOfFirst { it.id == reminderId }
            repository.deleteReminder(reminderId)
            repository.cancelReminder(reminderId)
            sendUiEvent(UiEvent.ShowSnackbar("Your reminder has been deleted", SnackbarDuration.Short))
            reminders.removeAt(index)
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

}