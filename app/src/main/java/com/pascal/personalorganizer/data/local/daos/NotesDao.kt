package com.pascal.personalorganizer.data.local.daos

import androidx.room.*
import com.pascal.personalorganizer.data.local.entities.NotesEntity
import com.pascal.personalorganizer.util.Constants.NOTES_TABLE

@Dao
interface NotesDao {

    @Query("SELECT * FROM $NOTES_TABLE")
    suspend fun getNotes(): List<NotesEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NotesEntity)

    @Update
    suspend fun updateNote(note: NotesEntity)

    @Query("DELETE FROM $NOTES_TABLE WHERE id = :noteId")
    suspend fun deleteNote(noteId: Int)

}