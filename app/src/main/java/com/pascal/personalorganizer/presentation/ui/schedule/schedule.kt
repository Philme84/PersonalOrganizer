package com.pascal.personalorganizer.presentation.ui.schedule

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pascal.personalorganizer.domain.models.CalendarModel
import com.pascal.personalorganizer.domain.models.Schedule
import com.pascal.personalorganizer.presentation.composables.ConfirmDeleteDialog
import com.pascal.personalorganizer.presentation.composables.EmptyLottie
import com.pascal.personalorganizer.presentation.composables.RemindersShimmer
import com.pascal.personalorganizer.presentation.composables.Shimmer
import com.pascal.personalorganizer.presentation.navigation.Screens
import com.pascal.personalorganizer.presentation.theme.Shapes
import com.pascal.personalorganizer.presentation.ui.MainViewModel
import com.pascal.personalorganizer.util.UiEvent
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun ScheduleScreen(
    mainViewModel: MainViewModel,
    onNavigate: (UiEvent.Navigate) -> Unit,
    onSnackBar: (UiEvent.ShowSnackbar) -> Unit,
    scheduleViewModel: ScheduleViewModel = hiltViewModel()
){

    LaunchedEffect(Unit){
        mainViewModel.setCurrentScreen(Screens.ScheduleScreen)
        scheduleViewModel.setCalendar(mainViewModel.monthOffset, mainViewModel.monthIndex)
        scheduleViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event)
                is UiEvent.ShowSnackbar -> onSnackBar(event)
                else -> Unit
            }
        }
    }

    val uiState = scheduleViewModel.uiState
    val calendarList = scheduleViewModel.calendarList
    val schedule = scheduleViewModel.schedules
    val listState = rememberLazyListState()
    val sdf1 = SimpleDateFormat("EEE", Locale.getDefault())
    val sdf2 = SimpleDateFormat("d", Locale.getDefault())
    var idToDelete by remember {
        mutableStateOf(0)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(modifier = Modifier.weight(0.15f), onClick = {
                mainViewModel.addMonthOffset(-1)
                scheduleViewModel.onEvent(ScheduleEvent.OnPrevMonth)
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIos,
                    tint = MaterialTheme.colors.primary,
                    contentDescription = null
                )
            }

            Text(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxWidth(),
                text = uiState.currentMonth,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.surface
            )

            IconButton(modifier = Modifier.weight(0.15f), onClick = {
                mainViewModel.addMonthOffset(1)
                scheduleViewModel.onEvent(ScheduleEvent.OnNextMonth)
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    tint = MaterialTheme.colors.primary,
                    contentDescription = null
                )
            }
        }
        LazyRow(state = listState) {
            itemsIndexed(items = calendarList) { index, calendar ->
                DayCell(calendar = calendar, sdf1 = sdf1, sdf2 = sdf2) {
                    mainViewModel.changeMonthIndex(index)
                    scheduleViewModel.onEvent(ScheduleEvent.OnDaySelection(
                        index)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Crossfade(targetState = uiState.isReady) { state ->
            when (state){
                false -> {
                    Shimmer {
                        RemindersShimmer(brush = it)
                    }
                }
                true -> {
                    if (schedule.isNotEmpty()){
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            items(schedule){
                                Column{
                                    ScheduleCell(schedule = it, onEdit = {
                                        scheduleViewModel.onEvent(ScheduleEvent.OnEditSchedule(
                                            Schedule(
                                                id = it.id,
                                                title = it.title,
                                                selectedDay = it.selectedDay,
                                                date = it.date,
                                                reminder = it.reminder
                                            )
                                        ))
                                    }, onDelete = {
                                        idToDelete = it.id
                                        scheduleViewModel.onEvent(ScheduleEvent.OnShowDeleteDialog)
                                    })
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    } else {
                        Column(modifier = Modifier.offset(y = (-50).dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            EmptyLottie()
                            Text(modifier = Modifier.fillMaxWidth().padding(16.dp), text = "Nothing here yet, start planning your schedule for the day...", textAlign = TextAlign.Center, style = TextStyle(
                                fontWeight = FontWeight.Medium, fontSize = 16.sp, color = MaterialTheme.colors.primary
                            ))
                        }
                    }
                }
            }
        }
    }

    if (mainViewModel.fabPress) {
        mainViewModel.setFabStatus(false)
        scheduleViewModel.onEvent(ScheduleEvent.AddNewSchedule)
    }

    if (uiState.showDialog){
        ConfirmDeleteDialog(text = "Are you sure you want to delete this schedule?", onDelete = {
            scheduleViewModel.onEvent(ScheduleEvent.OnDeleteSchedule(idToDelete))
        }) {
            scheduleViewModel.onEvent(ScheduleEvent.OnShowDeleteDialog)
        }
    }

    LaunchedEffect(uiState.calendarSettingIndicator) {
        if (uiState.calendarSettingIndicator && mainViewModel.monthIndex == -1) {
            delay(1000)
            listState.animateScrollToItem(index = uiState.currentDayIndex)
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScheduleCell(schedule: Schedule, onEdit: () -> Unit, onDelete: () -> Unit){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(56.dp),
        shape = Shapes.large,
        onClick = onEdit,
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp
    ) {
        Box(
            contentAlignment = Alignment.TopEnd
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 4.dp),
                    text = schedule.title,
                    textAlign = TextAlign.Start,
                    style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp, color = MaterialTheme.colors.onSurface),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = epochToCurrentHour(schedule.date),
                        style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, color = MaterialTheme.colors.onSurface),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    if (schedule.reminder){
                        Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colors.primary)
                    }
                }
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, tint = MaterialTheme.colors.secondary)
            }
        }

    }
}

private fun epochToCurrentHour(epochTime: Long): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val instant = Instant.ofEpochSecond(epochTime)
    val localDate = LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
    return localDate.format(formatter)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DayCell(
    calendar: CalendarModel,
    sdf1: SimpleDateFormat,
    sdf2: SimpleDateFormat,
    onClick: () -> Unit
) {
    val color = if (calendar.isSelected) {
        MaterialTheme.colors.secondary
    } else {
        MaterialTheme.colors.onPrimary
    }

    val dotColor = if (calendar.isCurrentDay) {
        Color.Green
    } else {
        Color.White
    }

    Card(
        modifier = Modifier
            .width(50.dp)
            .height(70.dp)
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
        backgroundColor = color,
        contentColor = Black,
        elevation = 4.dp
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Canvas(modifier = Modifier.size(5.dp), onDraw = {drawCircle(color = dotColor)})
            Text(
                text = sdf1.format(calendar.data.time).uppercase(),
                style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp),
                color = Black
            )
            Text(
                text = sdf2.format(calendar.data.time),
                style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp),
                color = Black
            )
        }
    }
}