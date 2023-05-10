package com.pascal.personalorganizer.presentation.ui.reminders

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.pascal.personalorganizer.domain.models.Reminder
import com.pascal.personalorganizer.presentation.composables.ConfirmDeleteDialog
import com.pascal.personalorganizer.presentation.composables.EmptyLottie
import com.pascal.personalorganizer.presentation.composables.RemindersShimmer
import com.pascal.personalorganizer.presentation.composables.Shimmer
import com.pascal.personalorganizer.presentation.navigation.Screens
import com.pascal.personalorganizer.presentation.ui.MainViewModel
import com.pascal.personalorganizer.presentation.theme.Shapes
import com.pascal.personalorganizer.util.UiEvent
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun RemindersScreen(
    mainViewModel: MainViewModel,
    onNavigate: (UiEvent.Navigate) -> Unit,
    onSnackBar: (UiEvent.ShowSnackbar) -> Unit,
    remindersViewModel: RemindersViewModel = hiltViewModel()
){

    LaunchedEffect(Unit){
        mainViewModel.setCurrentScreen(Screens.ReminderScreen)
        remindersViewModel.getReminders()
        remindersViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event)
                is UiEvent.ShowSnackbar -> onSnackBar(event)
                else -> Unit
            }
        }
    }

    val context = LocalContext.current
    val activity = context as Activity

    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else mutableStateOf(true)
    }

    var notificationPermissionRationale by remember { mutableStateOf(false)}

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
            if(!isGranted){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionRationale =
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    )

    val reminders = remindersViewModel.reminders
    val isReady = remindersViewModel.isReady
    var idToDelete by remember {
        mutableStateOf(0)
    }

    Crossfade(targetState = isReady) {state ->
        when(state){
            false -> {
                Shimmer {
                    RemindersShimmer(brush = it)
                }
            }
            true -> {
                if (reminders.isNotEmpty()){
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        items(reminders){
                            Column{
                                ReminderCell(reminder = it, onEdit = {
                                    remindersViewModel.onEvent(ReminderEvents.OnEditReminder(
                                        Reminder(
                                            id = it.id,
                                            title = it.title,
                                            date = it.date,
                                            repeatDaily = it.repeatDaily
                                        )
                                    ))
                                }, onDelete = {
                                    idToDelete = it.id
                                    remindersViewModel.onEvent(ReminderEvents.OnShowDeleteDialog)
                                })
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        EmptyLottie()
                        Text(text = "Nothing here yet, add a reminder...", style = TextStyle(
                            fontWeight = FontWeight.Medium, fontSize = 16.sp, color = MaterialTheme.colors.primary
                        ))
                    }
                }

                if (mainViewModel.fabPress) {
                    mainViewModel.setFabStatus(false)
                    remindersViewModel.onEvent(ReminderEvents.AddNewReminder)
                }

                if (remindersViewModel.showDialog){
                    ConfirmDeleteDialog(text = "Are you sure you want to delete this reminder?", onDelete = {
                        remindersViewModel.onEvent(ReminderEvents.OnDeleteReminder(idToDelete))
                    }) {
                        remindersViewModel.onEvent(ReminderEvents.OnShowDeleteDialog)
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (!hasNotificationPermission ){
                        LaunchedEffect(Unit){
                            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReminderCell(reminder: Reminder, onEdit: () -> Unit, onDelete: () -> Unit){
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
                    text = reminder.title,
                    textAlign = TextAlign.Start,
                    style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp, color = MaterialTheme.colors.onSurface),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = epochToCurrentDate(reminder.date, reminder.repeatDaily),
                        style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, color = MaterialTheme.colors.onSurface),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    if (reminder.repeatDaily){
                        Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, tint = MaterialTheme.colors.primary)
                    }
                }
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, tint = MaterialTheme.colors.secondary)
            }
        }
        
    }
}

private fun epochToCurrentDate(epochTime: Long, isRepeating: Boolean): String {
    val currentTime = System.currentTimeMillis()/1000L

    val formatter: DateTimeFormatter = if (isRepeating){
        if (currentTime < epochTime){
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        } else {
            DateTimeFormatter.ofPattern("HH:mm")
        }
    } else {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    }

    val instant = Instant.ofEpochSecond(epochTime)
    val localDate = LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
    return localDate.format(formatter)
}