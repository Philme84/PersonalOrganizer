package com.pascal.personalorganizer.presentation.ui.notes.add

import android.net.Uri

data class AddNotesUIState (
    val title : String = "",
    val titleError: Boolean = false,
    val description: String = "",
    val descriptionError: Boolean = false,
    val uri: Uri = Uri.EMPTY,
    val date: String = "",
    val isRecording: Boolean = false,
    val isPlaying: Boolean = false,
)