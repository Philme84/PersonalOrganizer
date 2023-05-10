package com.pascal.personalorganizer.presentation.ui.reminders.add

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
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
import com.pascal.personalorganizer.domain.models.Reminder
import com.pascal.personalorganizer.presentation.navigation.Screens
import com.pascal.personalorganizer.presentation.theme.Shapes
import com.pascal.personalorganizer.presentation.ui.MainViewModel
import com.pascal.personalorganizer.ui.theme.grey
import com.pascal.personalorganizer.util.UiEvent
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*


@Composable
fun AddReminderScreen(
    mainViewModel: MainViewModel,
    reminder: Reminder,
    onBack: (UiEvent.PopBackStack) -> Unit,
    onSnackBar: (UiEvent.ShowSnackbar) -> Unit,
    addReminderViewModel: AddReminderViewModel = hiltViewModel()
){

    LaunchedEffect(Unit) {
        mainViewModel.setCurrentScreen(Screens.AddReminderScreen)
        if (reminder.id != -1){
            addReminderViewModel.onEvent(AddReminderEvents.SetExistingInfo(reminder))
        }
        addReminderViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.PopBackStack -> onBack(event)
                is UiEvent.ShowSnackbar -> onSnackBar(event)
                else -> Unit
            }
        }
    }

    val uiState = addReminderViewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.CenterStart){
            Text(modifier = Modifier.fillMaxWidth(), text = if (reminder.id != -1){"Update Remind"} else {"Add New Reminder"}, textAlign = TextAlign.Center, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, color = MaterialTheme.colors.surface))
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
                IconButton(onClick = { addReminderViewModel.onEvent(AddReminderEvents.OnBackButton) }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp), text = "Reminder", textAlign = TextAlign.Start, style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, color = MaterialTheme.colors.surface))
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = uiState.title,
            singleLine = false,
            onValueChange = { addReminderViewModel.onEvent(AddReminderEvents.OnTitleChange(it)) },
            isError = uiState.titleError,
            label = {
                Text(
                    "Reminder",
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
            .padding(start = 2.dp), text = "Date", textAlign = TextAlign.Start, style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, color = MaterialTheme.colors.surface))
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
                Text(text = uiState.date, style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, color = grey))
                DatePicker(epoch = {
                    addReminderViewModel.onEvent(AddReminderEvents.OnDatePicker(it))
                })
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(modifier = Modifier.padding(start = 2.dp), text = "Repeat daily", style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, color = MaterialTheme.colors.surface))
            Spacer(modifier = Modifier.width(8.dp))
            Checkbox(checked = uiState.dailyCheck, onCheckedChange = {
                addReminderViewModel.onEvent(AddReminderEvents.OnDailyCheckedChange(it))
            },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colors.primary,
                uncheckedColor = grey
            ))
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {addReminderViewModel.onEvent(AddReminderEvents.AddNewReminder)},
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary
            )
        ) {
            Text(text = if (reminder.id != -1){"Update remind"} else {"Remind me"}, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colors.onPrimary))
        }
    }
}


@Composable
fun DatePicker(epoch:(Long) -> Unit){
    val context = LocalContext.current

    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    val hour = c.get(Calendar.HOUR_OF_DAY)
    val minute = c.get(Calendar.MINUTE)

    var pYear  = 0
    var pMonth  = 0
    var pDay  = 0
    var pHour = 0
    var pMinute = 0

    val timePicker = TimePickerDialog(
        context,
        {_, h : Int, m: Int ->
            pHour = h
            pMinute = m
            epoch(calculateEpoch(pYear, pMonth, pDay, pHour, pMinute))
        },
        hour,
        minute,
        true
    )

    val datePicker = DatePickerDialog(context, { view, pickedYear, monthOfYear, dayOfMonth ->
        pYear = pickedYear
        pMonth = monthOfYear + 1
        pDay = dayOfMonth
        timePicker.show()
    }, year, month, day)


    IconButton(
        onClick = {
            datePicker.show()
        }
    ) {
        Icon(imageVector = Icons.Default.CalendarMonth,contentDescription = null, tint = MaterialTheme.colors.primary )
    }
}

private fun calculateEpoch(y: Int, m: Int, d: Int, h: Int, min: Int): Long {
    val dateTime = LocalDateTime.of(y, m, d, h, min)
    return dateTime.toEpochSecond(ZoneOffset.UTC)
}