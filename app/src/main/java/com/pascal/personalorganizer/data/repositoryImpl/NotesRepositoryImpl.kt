package com.pascal.personalorganizer.data.repositoryImpl

import com.pascal.personalorganizer.data.local.daos.NotesDao
import com.pascal.personalorganizer.data.local.entities.toEntity
import com.pascal.personalorganizer.data.local.entities.toEntityForUpdate
import com.pascal.personalorganizer.domain.models.Notes
import com.pascal.personalorganizer.domain.models.toDomain
import com.pascal.personalorganizer.domain.repository.NotesRepository
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val dao: NotesDao
): NotesRepository {

    override suspend fun getAllNotes(): List<Notes> {
       return dao.getNotes().map { //We are getting a entity from our db, so wee need to map it to a object.
           it.toDomain()
       }
    }

    override suspend fun insertNewNote(notes: Notes) {
        dao.insertNote(notes.toEntity())// We need to convert our object to a entity so it can be inserted to the db
    }

    override suspend fun updateNote(notes: Notes) {
        dao.updateNote(notes.toEntityForUpdate())
    }

    override suspend fun deleteNote(noteId: Int) {
       dao.deleteNote(noteId)
    }
}