package com.pascal.personalorganizer.presentation.navigation

import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable

@Composable
fun Fab(onFabClick: () -> Unit) {
    FloatingActionButton(
        onClick = onFabClick,
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null)
    }
}