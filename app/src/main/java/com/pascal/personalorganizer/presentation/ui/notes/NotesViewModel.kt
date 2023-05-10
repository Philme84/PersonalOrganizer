package com.pascal.personalorganizer.presentation.ui.notes

import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pascal.personalorganizer.domain.models.Notes
import com.pascal.personalorganizer.domain.repository.NotesRepository
import com.pascal.personalorganizer.presentation.navigation.Screens
import com.pascal.personalorganizer.presentation.ui.reminders.ReminderEvents
import com.pascal.personalorganizer.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NotesRepository,
): ViewModel() {

    private val _uiEvent =  Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var notes = mutableStateListOf<Notes>()
        private set

    var isReady by mutableStateOf(false)
        private set

    var showDialog by mutableStateOf(false)
        private set

    fun onEvent(event: NotesEvent){
        when (event){
            is NotesEvent.AddNewNote -> {
                sendUiEvent(UiEvent.Navigate(Screens.AddNotesScreen.route + "/-1"))
            }
            is NotesEvent.OnEditNote -> {
                sendUiEvent(UiEvent.Navigate(Screens.AddNotesScreen.route + "/${event.notes.id}?title=${event.notes.title}&description=${event.notes.description}&date=${event.notes.date}"))
            }
            is NotesEvent.OnShowDeleteDialog -> {
                showDialog = !showDialog
            }
            is NotesEvent.OnDeleteNote -> {
                deleteNote(event.noteId)
            }
        }
    }


    fun getNotes(){
        viewModelScope.launch {
            notes = repository.getAllNotes().toMutableStateList()
            notes.sortedBy { it.date }
            isReady = true
        }
    }

    private fun deleteNote(noteId: Int){
        showDialog = !showDialog
        viewModelScope.launch {
            val index = notes.indexOfFirst { it.id == noteId }
            repository.deleteNote(noteId)
            sendUiEvent(UiEvent.ShowSnackbar("Your note has been deleted", SnackbarDuration.Short))
            notes.removeAt(index)
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

}