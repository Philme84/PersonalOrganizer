package com.pascal.personalorganizer.presentation.ui.reminders.add

import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pascal.personalorganizer.domain.models.Reminder
import com.pascal.personalorganizer.domain.repository.ReminderRepository
import com.pascal.personalorganizer.presentation.ui.schedule.add.AddScheduleUIState
import com.pascal.personalorganizer.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AddReminderViewModel @Inject constructor(
    private val repository: ReminderRepository
) : ViewModel(){

    private val _uiEvent =  Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var uiState by mutableStateOf(AddReminderUIState())
        private set

    private val title get() = uiState.title
    private val check get() = uiState.dailyCheck
    private var epoch = 0L
    private var id = -1

    fun onEvent(event: AddReminderEvents){
        when (event){
            is AddReminderEvents.OnTitleChange -> {
                uiState = uiState.copy(title = event.newValue)
            }
            is AddReminderEvents.OnDailyCheckedChange -> {
                uiState = uiState.copy(dailyCheck = event.newValue)
            }
            is AddReminderEvents.OnDatePicker -> {
                epoch = event.value
                val converterEpoch = epochToCurrentDate(epoch)
                uiState = uiState.copy(date = converterEpoch)
            }
            is AddReminderEvents.AddNewReminder -> {
                if (id != -1){
                    updateReminder(
                        Reminder(
                            id = id,
                            title = title,
                            date = epoch,
                            repeatDaily = check
                        )
                    )
                } else {
                    addReminder(
                        Reminder(
                            id = -1,
                            title = title,
                            date = epoch,
                            repeatDaily = check
                        )
                    )
                }
            }
            is AddReminderEvents.OnBackButton -> {
                sendUiEvent(UiEvent.PopBackStack)
            }
            is AddReminderEvents.SetExistingInfo -> {
                epoch = event.reminder.date
                id = event.reminder.id
                val converterEpoch = epochToCurrentDate(epoch)
                uiState = uiState.copy(title = event.reminder.title, dailyCheck = event.reminder.repeatDaily, date = converterEpoch)
            }
        }
    }

    private fun addReminder(reminder: Reminder){
        if (reminder.title.isBlank()){
            sendUiEvent(UiEvent.ShowSnackbar("You must provide a reminder", SnackbarDuration.Short))
            return
        }
        if (reminder.date == 0L){
            sendUiEvent(UiEvent.ShowSnackbar("You must provide a date", SnackbarDuration.Short))
            return
        }

        viewModelScope.launch {
            repository.insertNewReminder(reminder)
            repository.scheduleReminder(reminder.id, reminder.title, reminder.date, reminder.repeatDaily)
            sendUiEvent(UiEvent.PopBackStack)
            sendUiEvent(UiEvent.ShowSnackbar("Reminder created successfully", SnackbarDuration.Short))
        }
    }

    private fun updateReminder(reminder: Reminder){
        if (reminder.title.isBlank()){
            sendUiEvent(UiEvent.ShowSnackbar("You must provide a reminder", SnackbarDuration.Short))
            return
        }
        if (reminder.date == 0L){
            sendUiEvent(UiEvent.ShowSnackbar("You must provide a date", SnackbarDuration.Short))
            return
        }

        viewModelScope.launch {
            repository.updateReminder(reminder)
            repository.cancelReminder(reminder.id)
            repository.scheduleReminder(reminder.id, reminder.title, reminder.date, reminder.repeatDaily)
            sendUiEvent(UiEvent.PopBackStack)
            sendUiEvent(UiEvent.ShowSnackbar("Reminder updated successfully", SnackbarDuration.Short))
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private fun epochToCurrentDate(epochTime: Long): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val instant = Instant.ofEpochSecond(epochTime)
        val localDate = LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
        return localDate.format(formatter)
    }
}