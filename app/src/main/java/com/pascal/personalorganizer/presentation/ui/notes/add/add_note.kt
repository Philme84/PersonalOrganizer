package com.pascal.personalorganizer.presentation.ui.notes.add

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.pascal.personalorganizer.domain.models.Notes
import com.pascal.personalorganizer.presentation.navigation.Screens
import com.pascal.personalorganizer.presentation.theme.Shapes
import com.pascal.personalorganizer.presentation.ui.MainViewModel
import com.pascal.personalorganizer.ui.theme.grey
import com.pascal.personalorganizer.util.UiEvent
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun AddNoteScreen(
    mainViewModel: MainViewModel,
    notes: Notes,
    onBack: (UiEvent.PopBackStack) -> Unit,
    onSnackBar: (UiEvent.ShowSnackbar) -> Unit,
    addNotesViewModel: AddNotesViewModel = hiltViewModel()
){

    LaunchedEffect(Unit) {
        mainViewModel.setCurrentScreen(Screens.AddNotesScreen)
        if (notes.id != -1){
            addNotesViewModel.onEvent(AddNoteEvents.SetExistingInfo(notes))
        }
        addNotesViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.PopBackStack -> onBack(event)
                is UiEvent.ShowSnackbar -> onSnackBar(event)
                else -> Unit
            }
        }
    }

    val context = LocalContext.current
    val activity = context as Activity

    var hasNotificationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var notificationPermissionRationale by remember { mutableStateOf(false)}

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
            if(!isGranted){
                notificationPermissionRationale =
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)
            }
        }
    )

    val uiState = addNotesViewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.CenterStart){
            Text(modifier = Modifier.fillMaxWidth(), text = if (notes.id != -1){"Update Note"} else {"Add New Note"}, textAlign = TextAlign.Center, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, color = MaterialTheme.colors.surface))
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
                IconButton(onClick = { addNotesViewModel.onEvent(AddNoteEvents.OnBackButton) }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp), text = uiState.date, textAlign = TextAlign.Start, style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, color = MaterialTheme.colors.primary))
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = uiState.title,
            singleLine = false,

            onValueChange = { addNotesViewModel.onEvent(AddNoteEvents.OnTitleChange(it)) },
            isError = uiState.titleError,
            label = {
                Text(
                    "Title",
                    style = MaterialTheme.typography.h4,
                    color = grey
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            shape = Shapes.large,
            textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = MaterialTheme.colors.background,
                unfocusedBorderColor = MaterialTheme.colors.background,
                focusedBorderColor = MaterialTheme.colors.background,
                textColor = Color.Black
            ),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = uiState.description,
            singleLine = false,
            onValueChange = { addNotesViewModel.onEvent(AddNoteEvents.OnDescriptionChange(it)) },
            isError = uiState.descriptionError,
            label = {
                Text(
                    "Description",
                    style = MaterialTheme.typography.h4,
                    color = grey
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            shape = Shapes.large,
            textStyle = MaterialTheme.typography.h4,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = MaterialTheme.colors.background,
                unfocusedBorderColor = MaterialTheme.colors.background,
                focusedBorderColor = MaterialTheme.colors.background,
                textColor = Color.Black
            ),
        )
        Spacer(modifier = Modifier.height(250.dp))
        if (!uiState.isRecording){
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if (hasNotificationPermission){
                        addNotesViewModel.onEvent(AddNoteEvents.OnStartRecording)
                    } else {
                        addNotesViewModel.noPermissions()
                    }

                }) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary
                    )
                }
                Text(modifier = Modifier.padding(start = 2.dp), text = "Start recording", style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, color = MaterialTheme.colors.surface))
                Spacer(modifier = Modifier.width(4.dp))
                if (uiState.uri != Uri.EMPTY){
                    if (uiState.isPlaying){
                        IconButton(onClick = {
                            addNotesViewModel.onEvent(AddNoteEvents.OnStopPlaying)
                        }) {
                            Icon(
                                imageVector = Icons.Default.StopCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colors.primary
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            addNotesViewModel.onEvent(AddNoteEvents.OnStartPlaying)
                        }) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = MaterialTheme.colors.primary
                            )
                        }
                    }
                }
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    addNotesViewModel.onEvent(AddNoteEvents.OnStopRecording)
                }) {
                    Icon(
                        imageVector = Icons.Default.MicOff,
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary
                    )
                }
                Text(modifier = Modifier.padding(start = 2.dp), text = "Stop recording", style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, color = MaterialTheme.colors.surface))
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
        Button(
            onClick = {addNotesViewModel.onEvent(AddNoteEvents.AddNewNote)},
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary
            )
        ) {
            Text(text = if (notes.id != -1){"Update note"} else {"Save note"}, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colors.onPrimary))
        }
    }

    if (!hasNotificationPermission ){
        LaunchedEffect(Unit){
            launcher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
}
