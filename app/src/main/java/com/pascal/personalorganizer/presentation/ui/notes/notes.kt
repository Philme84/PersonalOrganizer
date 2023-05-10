package com.pascal.personalorganizer.presentation.ui.notes

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.pascal.personalorganizer.domain.models.Notes
import com.pascal.personalorganizer.presentation.composables.ConfirmDeleteDialog
import com.pascal.personalorganizer.presentation.composables.EmptyLottie
import com.pascal.personalorganizer.presentation.composables.RemindersShimmer
import com.pascal.personalorganizer.presentation.composables.Shimmer
import com.pascal.personalorganizer.presentation.navigation.Screens
import com.pascal.personalorganizer.presentation.theme.Shapes
import com.pascal.personalorganizer.presentation.ui.MainViewModel
import com.pascal.personalorganizer.util.UiEvent
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun NotesScreen(
    mainViewModel: MainViewModel,
    onNavigate: (UiEvent.Navigate) -> Unit,
    onSnackBar: (UiEvent.ShowSnackbar) -> Unit,
    notesViewModel: NotesViewModel = hiltViewModel()
){

    LaunchedEffect(Unit){
        mainViewModel.setCurrentScreen(Screens.NotesScreen)
        notesViewModel.getNotes()
        notesViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event)
                is UiEvent.ShowSnackbar -> onSnackBar(event)
                else -> Unit
            }
        }
    }

    val context = LocalContext.current
    val activity = context as Activity

    /**
     * We need certain permissions, for example in order to record voice notes we need
     * access to the record audio permission. We are using rememberLauncherForActivityResult
     * to ask for permissions.
     */

    var hasRecordPermission by remember {
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
            hasRecordPermission = isGranted
            if(!isGranted){
                notificationPermissionRationale =
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)
            }
        }
    )

    val notes = notesViewModel.notes
    val isReady = notesViewModel.isReady
    var idToDelete by remember {
        mutableStateOf(0)
    }

    Crossfade(targetState = isReady) { state ->
        when(state){
            false -> {
                Shimmer {
                    RemindersShimmer(brush = it)
                }
            }
            true -> {
                if (notes.isNotEmpty()){
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(notes) {
                            Column{
                                NoteCell(notes = it, onEdit = {
                                    mainViewModel.setUri(it.uri)
                                    notesViewModel.onEvent(
                                        NotesEvent.OnEditNote(
                                        Notes(
                                            id = it.id,
                                            title = it.title,
                                            description = it.description,
                                            date = it.date,
                                            uri = it.uri
                                        )
                                    ))
                                }) {
                                    idToDelete = it.id
                                    notesViewModel.onEvent(NotesEvent.OnShowDeleteDialog)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        EmptyLottie()
                        Text(text = "Nothing here yet, start writing some notes...", style = TextStyle(
                            fontWeight = FontWeight.Medium, fontSize = 16.sp, color = MaterialTheme.colors.primary
                        )
                        )
                    }
                }

                if (mainViewModel.fabPress) {
                    mainViewModel.setFabStatus(false)
                    notesViewModel.onEvent(NotesEvent.AddNewNote)
                }

                if (notesViewModel.showDialog){
                    ConfirmDeleteDialog(text = "Are you sure you want to delete this note?", onDelete = {
                        notesViewModel.onEvent(NotesEvent.OnDeleteNote(idToDelete))
                    }) {
                        notesViewModel.onEvent(NotesEvent.OnShowDeleteDialog)
                    }
                }


                if (!hasRecordPermission ){
                    LaunchedEffect(Unit){
                        launcher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }

            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NoteCell(notes: Notes, onEdit: () -> Unit, onDelete: () -> Unit){
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
                    text = notes.title,
                    textAlign = TextAlign.Start,
                    style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp, color = MaterialTheme.colors.onSurface),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 4.dp),
                    text = notes.description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, color = MaterialTheme.colors.onSurface),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = epochToCurrentDate(notes.date/1000L),
                        style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, color = MaterialTheme.colors.onSurface),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    if (notes.uri != Uri.EMPTY){
                        Icon(imageVector = Icons.Default.GraphicEq, contentDescription = null, tint = MaterialTheme.colors.primary)
                    }
                }
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, tint = MaterialTheme.colors.secondary)
            }
        }

    }
}

private fun epochToCurrentDate(epochTime: Long): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val instant = Instant.ofEpochSecond(epochTime)
    val localDate = LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
    return localDate.format(formatter)
}