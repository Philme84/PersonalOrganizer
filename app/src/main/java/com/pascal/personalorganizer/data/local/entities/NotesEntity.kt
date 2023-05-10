package com.pascal.personalorganizer.data.local.entities

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.pascal.personalorganizer.data.local.typeconverters.NotesTypeConverter
import com.pascal.personalorganizer.domain.models.Notes
import com.pascal.personalorganizer.util.Constants

@Entity(tableName = Constants.NOTES_TABLE)
class NotesEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "date") val date: Long,
    @TypeConverters(NotesTypeConverter::class) val uri: Uri,
)

/**
 * Wee need to map our Weather object to a Entity so it can be inserted to our room database.
 * ToEntity without id because it is given by room when we insert to the db.
 * ToEntityForUpdate with id because room needs it to know what to update
 */
fun Notes.toEntity() = NotesEntity(
    title = title,
    description = description,
    date = date,
    uri = uri
)

fun Notes.toEntityForUpdate() = NotesEntity(
    id = id,
    title = title,
    description = description,
    date = date,
    uri = uri
)