package com.pascal.personalorganizer.presentation.ui.schedule.add

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pascal.personalorganizer.domain.models.Schedule
import com.pascal.personalorganizer.presentation.navigation.Screens
import com.pascal.personalorganizer.presentation.theme.Shapes
import com.pascal.personalorganizer.presentation.ui.MainViewModel
import com.pascal.personalorganizer.ui.theme.grey
import com.pascal.personalorganizer.util.UiEvent
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Composable
fun AddScheduleScreen(
    mainViewModel: MainViewModel,
    schedule: Schedule,
    year: Int,
    month: Int,
    day: Int,
    onBack: (UiEvent.PopBackStack) -> Unit,
    onSnackBar: (UiEvent.ShowSnackbar) -> Unit,
    addScheduleViewModel: AddScheduleViewModel = hiltViewModel()
){

    LaunchedEffect(Unit) {
        mainViewModel.setCurrentScreen(Screens.AddScheduleScreen)
        if (schedule.id != -1){
            addScheduleViewModel.onEvent(AddScheduleEvents.SetExistingInfo(schedule))
        }
        addScheduleViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.PopBackStack -> onBack(event)
                is UiEvent.ShowSnackbar -> onSnackBar(event)
                else -> Unit
            }
        }
    }

    val uiState = addScheduleViewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.CenterStart){
            Text(modifier = Modifier.fillMaxWidth(), text = if (schedule.id != -1){"Update Schedule"} else {"Add New Schedule"}, textAlign = TextAlign.Center, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, color = MaterialTheme.colors.surface))
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(8.dp)
                    .offset(x = (-2).dp),
                color = MaterialTheme.colors.surface,
                shape = Shapes.large,
                elevation = 0.dp
            ) {
                IconButton(onClick = { addScheduleViewModel.onEvent(AddScheduleEvents.OnBackButton) }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp), text = "Schedule", textAlign = TextAlign.Start, style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, color = MaterialTheme.colors.surface)
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = uiState.title,
            singleLine = false,
            onValueChange = { addScheduleViewModel.onEvent(AddScheduleEvents.OnTitleChange(it)) },
            isError = uiState.titleError,
            label = {
                Text(
                    "Schedule",
                    style = MaterialTheme.typography.h4,
                    color = grey
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            shape = Shapes.large,
            textStyle = MaterialTheme.typography.h4,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = MaterialTheme.colors.onSurface,
                unfocusedBorderColor = MaterialTheme.colors.onSurface,
                focusedBorderColor = MaterialTheme.colors.primary,
                textColor = Color.Black
            ),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp), text = "Time", textAlign = TextAlign.Start, style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, color = MaterialTheme.colors.surface)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .heightIn(56.dp),
            color = MaterialTheme.colors.onBackground,
            shape = Shapes.large,
            elevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = uiState.hour, style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, color = grey))
                TimePicker(year, month, day, epoch = {
                    addScheduleViewModel.onEvent(AddScheduleEvents.OnTimePicker(it))
                })
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(modifier = Modifier.padding(start = 2.dp), text = "Remind me", style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, color = MaterialTheme.colors.surface))
            Spacer(modifier = Modifier.width(8.dp))
            Checkbox(checked = uiState.reminderCheck, onCheckedChange = {
                addScheduleViewModel.onEvent(AddScheduleEvents.OnReminderCheckedChange(it))
            },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colors.primary,
                    uncheckedColor = grey
                ))
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {addScheduleViewModel.onEvent(AddScheduleEvents.AddNewSchedule)},
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary
            )
        ) {
            Text(text = if (schedule.id != -1){"Update schedule"} else {"Schedule"}, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colors.onPrimary))
        }
    }
}

@Composable
fun TimePicker(year: Int, month: Int, day: Int, epoch:(Long) -> Unit){
    val context = LocalContext.current

    val c = Calendar.getInstance()
    val hour = c.get(Calendar.HOUR_OF_DAY)
    val minute = c.get(Calendar.MINUTE)

    var pHour = 0
    var pMinute = 0

    val timePicker = TimePickerDialog(
        context,
        {_, h : Int, m: Int ->
            pHour = h
            pMinute = m
            epoch(calculateEpoch(year, month, day, pHour, pMinute))
        },
        hour,
        minute,
        true
    )


    IconButton(
        onClick = {
            timePicker.show()
        }
    ) {
        Icon(imageVector = Icons.Default.Schedule,contentDescription = null, tint = MaterialTheme.colors.primary )
    }
}

private fun calculateEpoch(y: Int, m: Int, d: Int, h: Int, min: Int): Long {
    val dateTime = LocalDateTime.of(y, m, d, h, min)
    return dateTime.toEpochSecond(ZoneOffset.UTC)
}
