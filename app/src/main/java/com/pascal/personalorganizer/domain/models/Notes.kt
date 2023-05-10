package com.pascal.personalorganizer.domain.models

import android.net.Uri
import com.pascal.personalorganizer.data.local.entities.NotesEntity

data class Notes(
    val id: Int,
    val title: String,
    val description: String,
    val date: Long,
    val uri: Uri,
)

/**
 * Every time we retrieve data from room database it will return a entity,
 * so we need to map the entity to our Notes object that is the one being
 * use by our presentation layer
 */
fun NotesEntity.toDomain() = Notes(
    id = id,
    title = title,
    description = description,
    date = date,
    uri = uri,
)