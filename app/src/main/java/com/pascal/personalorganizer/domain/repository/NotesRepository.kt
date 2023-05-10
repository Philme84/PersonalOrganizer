package com.pascal.personalorganizer.domain.repository

import com.pascal.personalorganizer.domain.models.Notes

interface NotesRepository {

    suspend fun getAllNotes(): List<Notes>
    suspend fun insertNewNote(notes: Notes)
    suspend fun updateNote(notes: Notes)
    suspend fun deleteNote(noteId: Int)


}