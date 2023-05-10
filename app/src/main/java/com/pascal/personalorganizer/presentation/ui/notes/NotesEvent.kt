package com.pascal.personalorganizer.presentation.ui.notes

import com.pascal.personalorganizer.domain.models.Notes

sealed class NotesEvent {

    object AddNewNote: NotesEvent()
    data class OnEditNote(val notes: Notes): NotesEvent()
    object OnShowDeleteDialog: NotesEvent()
    data class OnDeleteNote(val noteId: Int): NotesEvent()

}