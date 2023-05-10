package com.pascal.personalorganizer.data.local.typeconverters

import android.net.Uri
import androidx.room.TypeConverter

class NotesTypeConverter {
    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun toUri(string: String?): Uri? {
        return string?.let { Uri.parse(it) }
    }
}