package com.pascal.personalorganizer.presentation.ui.notes.add

import com.pascal.personalorganizer.domain.models.Notes


sealed class AddNoteEvents {
    object AddNewNote: AddNoteEvents()
    data class OnTitleChange(val newValue: String): AddNoteEvents()
    data class OnDescriptionChange(val newValue: String): AddNoteEvents()
    object OnStartRecording: AddNoteEvents()
    object OnStopRecording: AddNoteEvents()
    object OnStartPlaying: AddNoteEvents()
    object OnStopPlaying: AddNoteEvents()
    object OnBackButton: AddNoteEvents()
    data class SetExistingInfo(val notes: Notes): AddNoteEvents()
}