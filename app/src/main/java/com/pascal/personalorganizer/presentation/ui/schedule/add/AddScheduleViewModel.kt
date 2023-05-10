package com.pascal.personalorganizer.presentation.ui.schedule.add

import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pascal.personalorganizer.domain.models.Reminder
import com.pascal.personalorganizer.domain.models.Schedule
import com.pascal.personalorganizer.domain.repository.ScheduleRepository
import com.pascal.personalorganizer.presentation.ui.reminders.add.AddReminderEvents
import com.pascal.personalorganizer.presentation.ui.reminders.add.AddReminderUIState
import com.pascal.personalorganizer.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository
): ViewModel() {

    private val _uiEvent =  Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var uiState by mutableStateOf(AddScheduleUIState())
        private set

    private val title get() = uiState.title
    private val check get() = uiState.reminderCheck
    private var epoch = 0L
    private var id = -1


    fun onEvent(event: AddScheduleEvents){
        when (event){
            is AddScheduleEvents.OnTitleChange -> {
                uiState = uiState.copy(title = event.newValue)
            }
            is AddScheduleEvents.OnTimePicker -> {
                epoch = event.value
                val converterEpoch = epochToCurrentHour(epoch)
                uiState = uiState.copy(hour = converterEpoch)
            }
            is AddScheduleEvents.OnReminderCheckedChange -> {
                uiState = uiState.copy(reminderCheck = event.newValue)
            }
            is AddScheduleEvents.AddNewSchedule -> {
                if (id != -1){
                    updateSchedule(
                        Schedule(
                            id = id,
                            title = title,
                            selectedDay = epochToCurrentDate(epoch),
                            date = epoch,
                            reminder = check
                        )
                    )
                } else {
                    addSchedule(
                        Schedule(
                            id = -1,
                            title = title,
                            selectedDay = epochToCurrentDate(epoch),
                            date = epoch,
                            reminder = check
                        )
                    )
                }
            }
            is AddScheduleEvents.OnBackButton -> {
                sendUiEvent(UiEvent.PopBackStack)
            }
            is AddScheduleEvents.SetExistingInfo -> {
                epoch = event.schedule.date
                id = event.schedule.id
                val converterEpoch = epochToCurrentDate(epoch)
                uiState = uiState.copy(title = event.schedule.title, reminderCheck = event.schedule.reminder, hour = converterEpoch)
            }
        }
    }


    private fun addSchedule(schedule: Schedule){
        if (schedule.title.isBlank()){
            sendUiEvent(UiEvent.ShowSnackbar("You must provide a schedule", SnackbarDuration.Short))
            return
        }
        if (schedule.date == 0L){
            sendUiEvent(UiEvent.ShowSnackbar("You must provide a time", SnackbarDuration.Short))
            return
        }

        viewModelScope.launch {
            repository.insertSchedule(schedule)
            repository.scheduleSchedule(schedule.id, schedule.title, schedule.date)
            sendUiEvent(UiEvent.PopBackStack)
            sendUiEvent(UiEvent.ShowSnackbar("Schedule created successfully", SnackbarDuration.Short))
        }
    }

    private fun updateSchedule(schedule: Schedule){
        if (schedule.title.isBlank()){
            sendUiEvent(UiEvent.ShowSnackbar("You must provide a schedule", SnackbarDuration.Short))
            return
        }
        if (schedule.date == 0L){
            sendUiEvent(UiEvent.ShowSnackbar("You must provide a time", SnackbarDuration.Short))
            return
        }

        viewModelScope.launch {
            repository.updateSchedule(schedule)
            repository.cancelSchedule(schedule.id)
            repository.scheduleSchedule(schedule.id, schedule.title, schedule.date)
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
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val instant = Instant.ofEpochSecond(epochTime)
        val localDate = LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
        return localDate.format(formatter)
    }

    private fun epochToCurrentHour(epochTime: Long): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val instant = Instant.ofEpochSecond(epochTime)
        val localDate = LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
        return localDate.format(formatter)
    }
}