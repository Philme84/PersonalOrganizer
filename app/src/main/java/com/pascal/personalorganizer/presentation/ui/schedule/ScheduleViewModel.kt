package com.pascal.personalorganizer.presentation.ui.schedule

import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pascal.personalorganizer.domain.models.CalendarModel
import com.pascal.personalorganizer.domain.models.Schedule
import com.pascal.personalorganizer.domain.repository.ScheduleRepository
import com.pascal.personalorganizer.presentation.navigation.Screens
import com.pascal.personalorganizer.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository
): ViewModel() {

    private val _uiEvent =  Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var schedules = mutableStateListOf<Schedule>()
        private set

    var calendarList = mutableStateListOf<CalendarModel>()
        private set

    var uiState by mutableStateOf(ScheduleUIState())
        private set

    private val cal = Calendar.getInstance(Locale.getDefault())
    private val cal2 = Calendar.getInstance(Locale.getDefault())

    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    private val sdf1 = SimpleDateFormat("MMMM yyyy EEEE d", Locale.getDefault())
    private val sdf2 = SimpleDateFormat("d'-'MM'-'yyyy", Locale.getDefault())

    private var dates = mutableStateListOf<Date>()
    private val currentDay = sdf1.format(cal.time)
    private var selectedDay = sdf2.format(cal.time)

    private val prevSelectedIndex get() = uiState.prevSelectedIndex
    private val showDialog get() = uiState.showDialog

    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun onEvent(event: ScheduleEvent){
        when (event){
            is ScheduleEvent.OnNextMonth -> {
                cal.add(Calendar.MONTH, +1)
                setCalendar(null, null)
            }
            is ScheduleEvent.OnPrevMonth -> {
                cal.add(Calendar.MONTH, -1)
                setCalendar(null, null)
            }
            is ScheduleEvent.OnDaySelection -> {
                if (event.index != prevSelectedIndex){
                    uiState = uiState.copy(isReady = false)
                    calendarList[event.index].isSelected = true
                    calendarList[prevSelectedIndex].isSelected = false

                    uiState = uiState.copy(prevSelectedIndex = event.index)

                    selectedDay = sdf2.format(calendarList[event.index].data.time)
                    addScheduleData(formatter.format(calendarList[prevSelectedIndex].data))
                }
            }
            is ScheduleEvent.AddNewSchedule -> {
                cal2.time = calendarList[prevSelectedIndex].data
                val year = cal2.get(Calendar.YEAR)
                val month = cal2.get(Calendar.MONTH) + 1
                val day = cal2.get(Calendar.DAY_OF_MONTH)
                sendUiEvent(UiEvent.Navigate(Screens.AddScheduleScreen.route + "/-1?year=$year&month=$month&day=$day"))
            }
            is ScheduleEvent.OnEditSchedule -> {
                cal2.time = calendarList[prevSelectedIndex].data
                val year = cal2.get(Calendar.YEAR)
                val month = cal2.get(Calendar.MONTH) + 1
                val day = cal2.get(Calendar.DAY_OF_MONTH)
                sendUiEvent(UiEvent.Navigate(Screens.AddScheduleScreen.route + "/${event.schedule.id}?title=${event.schedule.title}&selectedDay=${event.schedule.selectedDay}&date=${event.schedule.date}&reminder=${event.schedule.reminder}&year=$year&month=$month&day=$day"))
            }
            is ScheduleEvent.OnShowDeleteDialog -> {
                uiState = uiState.copy(showDialog = !showDialog)
            }
            is ScheduleEvent.OnDeleteSchedule -> {
               deleteSchedule(event.scheduleId)
            }
        }
    }

    fun addScheduleData(selectedDay: String){
        viewModelScope.launch {
            schedules = repository.getAllSchedules(selectedDay).toMutableStateList()
            schedules.sortedBy { it.date }
            uiState = uiState.copy(isReady = true)
        }
    }

    private fun deleteSchedule(scheduleId: Int){
        uiState = uiState.copy(showDialog = !showDialog)
        viewModelScope.launch {
            val index = schedules.indexOfFirst { it.id == scheduleId }
            repository.deleteSchedule(scheduleId)
            repository.cancelSchedule(scheduleId)
            sendUiEvent(UiEvent.ShowSnackbar("Your reminder has been deleted", SnackbarDuration.Short))
            schedules.removeAt(index)
        }
    }

    fun setCalendar(offset: Int?, index: Int?){

        if (offset != 0 && offset != null){
            cal.add(Calendar.MONTH, +offset)
        }

        uiState = uiState.copy(currentMonth = sdf.format(cal.time), calendarSettingIndicator = false)

        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        dates.clear()
        calendarList.clear()
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)

        var wasCurrentHandled = false

        while (dates.size < maxDaysInMonth) {
            dates.add(monthCalendar.time)

            if(currentDay == sdf1.format(monthCalendar.time)){
                calendarList.add(CalendarModel(monthCalendar.time,
                    isCurrentDay = true,
                    isSelected = true
                ))
                uiState = uiState.copy(currentDayIndex = calendarList.size - 1, prevSelectedIndex = calendarList.size - 1 )
                wasCurrentHandled = true
            } else {
                calendarList.add(CalendarModel(monthCalendar.time,
                    isCurrentDay = false,
                    isSelected = false
                ))
            }
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        if (!wasCurrentHandled){
            calendarList[0].isSelected = true
            uiState = uiState.copy(currentDayIndex = 0, prevSelectedIndex = 0 )
        }

        if (index != -1 && index != null){
            calendarList[prevSelectedIndex].isSelected = false
            calendarList[index].isSelected = true
            uiState = uiState.copy(currentDayIndex = index, prevSelectedIndex = index )
        }

        uiState = uiState.copy(calendarSettingIndicator = true)
        if (index != -1 && index != null){
            addScheduleData(formatter.format(calendarList[index].data))
        } else {
            addScheduleData(formatter.format(calendarList[prevSelectedIndex].data))
        }

    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}