package com.pascal.personalorganizer.presentation.ui.notes.add

import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pascal.personalorganizer.domain.models.Notes
import com.pascal.personalorganizer.domain.repository.NotesRepository
import com.pascal.personalorganizer.domain.repository.RecorderRepository
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
class AddNotesViewModel @Inject constructor(
    private val recorderRepository: RecorderRepository,
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val _uiEvent =  Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var uiState by mutableStateOf(AddNotesUIState())
        private set

    private val title get() = uiState.title
    private val description get() = uiState.description
    private val uri get() = uiState.uri
    private val isRecording get() = uiState.isRecording

    private var id = -1
    private var currentTime = System.currentTimeMillis()
    private var epoch = currentTime

    init {
        uiState = uiState.copy(date = epochToCurrentDate(epoch/1000L))
    }

    fun onEvent(event: AddNoteEvents){
        when (event){
            is AddNoteEvents.OnTitleChange -> {
                uiState = uiState.copy(title = event.newValue)
            }
            is AddNoteEvents.OnDescriptionChange -> {
                uiState = uiState.copy(description = event.newValue)
            }
            is AddNoteEvents.OnStartRecording -> {
                uiState = uiState.copy(isRecording = true)
                recorderRepository.startRecord(currentTime.toString())
            }
            is AddNoteEvents.OnStopRecording -> {
                uiState = uiState.copy(isRecording = false)
                recorderRepository.stopRecord(uri = {
                    uiState = uiState.copy(uri = it)
                })
            }
            is AddNoteEvents.OnStartPlaying -> {
                uiState = uiState.copy(isPlaying = true)
                recorderRepository.playFile(uri)
            }
            is AddNoteEvents.OnStopPlaying -> {
                uiState = uiState.copy(isPlaying = false)
                recorderRepository.stop()
            }
            is AddNoteEvents.AddNewNote -> {

                if (isRecording){
                    uiState = uiState.copy(isRecording = false)
                    recorderRepository.stopRecord(uri = {
                        uiState = uiState.copy(uri = it)
                    })
                }

                if (id != -1){
                    updateNote(
                        Notes(
                            id = id,
                            title = title,
                            description = description,
                            date = epoch,
                            uri = uri
                        )
                    )
                } else {
                    addNote(
                        Notes(
                            id = -1,
                            title = title,
                            description = description,
                            date = epoch,
                            uri = uri
                        )
                    )
                }
            }
            is AddNoteEvents.OnBackButton -> {
                sendUiEvent(UiEvent.PopBackStack)
            }
            is AddNoteEvents.SetExistingInfo -> {
                id = event.notes.id
                epoch = event.notes.date
                val converterEpoch = epochToCurrentDate(epoch/1000L)
                uiState = uiState.copy(title = event.notes.title, description = event.notes.description, uri = event.notes.uri, date = converterEpoch)
            }
        }
    }

    private fun updateNote(notes: Notes){
        if (notes.title.isBlank()){
            sendUiEvent(UiEvent.ShowSnackbar("You must provide a title", SnackbarDuration.Short))
            return
        }

        viewModelScope.launch {
            notesRepository.updateNote(notes)
            sendUiEvent(UiEvent.PopBackStack)
            sendUiEvent(UiEvent.ShowSnackbar("Note updated successfully", SnackbarDuration.Short))
        }
    }

    fun noPermissions(){
        sendUiEvent(UiEvent.ShowSnackbar("You need to grant microphone permissions to this application first", SnackbarDuration.Short))
    }

    private fun addNote(notes: Notes){
        if (notes.title.isBlank()){
            sendUiEvent(UiEvent.ShowSnackbar("You must provide a title", SnackbarDuration.Short))
            return
        }

        viewModelScope.launch {
            notesRepository.insertNewNote(notes)
            sendUiEvent(UiEvent.PopBackStack)
            sendUiEvent(UiEvent.ShowSnackbar("Note created successfully", SnackbarDuration.Short))
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


}