package com.pascal.personalorganizer.presentation.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pascal.personalorganizer.presentation.theme.Shapes
import com.pascal.personalorganizer.ui.theme.backgroundGrey

@Composable
fun ConfirmDeleteDialog(text : String, onDelete: () -> Unit, onDismiss: () -> Unit) {

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 15.dp, vertical = 10.dp),
            shape = Shapes.large,
            color = backgroundGrey
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 15.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    text = text,
                    textAlign = TextAlign.Justify,
                    style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp),
                    color = MaterialTheme.colors.surface
                )
                Spacer(modifier = Modifier.height(32.dp))
                Row {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        onClick = onDismiss,
                        shape = Shapes.large,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.surface,
                            contentColor = MaterialTheme.colors.onSurface,
                        )
                    ) {
                        Text(
                            text = "Cancelar",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colors.onPrimary)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .padding(start = 5.dp),
                        onClick = onDelete,
                        shape = Shapes.large,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.onPrimary,
                            contentColor = MaterialTheme.colors.secondary,
                        )
                    ) {
                        Text(
                            text = "Eliminar",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colors.secondary)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}