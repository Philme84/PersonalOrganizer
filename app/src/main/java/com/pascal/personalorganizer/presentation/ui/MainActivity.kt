package com.pascal.personalorganizer.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.pascal.personalorganizer.presentation.navigation.BottomBar
import com.pascal.personalorganizer.presentation.navigation.Fab
import com.pascal.personalorganizer.presentation.navigation.NavigationController
import com.pascal.personalorganizer.presentation.theme.PersonalOrganizerTheme
import com.pascal.personalorganizer.presentation.theme.Shapes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val mainViewModel by viewModels<MainViewModel>()

        setContent {
            PersonalOrganizerTheme {
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                
                Scaffold(
                    scaffoldState = scaffoldState,
                    bottomBar = { BottomBar(screen = mainViewModel.currentScreen.value, navHostController = navController)},
                    backgroundColor = MaterialTheme.colors.background,
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState, snackbar = {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Snackbar (
                                shape = Shapes.large,
                                backgroundColor = MaterialTheme.colors.primary,
                                contentColor = MaterialTheme.colors.onPrimary,
                                content = {
                                    Text(text = it.message, style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp))
                                }
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    })},
                    floatingActionButton = {
                        if (mainViewModel.currentScreen.value.fab) {
                            Fab(
                                onFabClick = { mainViewModel.setFabStatus(true) }
                            )
                        }
                    }
                ) {padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        NavigationController(
                            navController = navController,
                            viewModel = mainViewModel,
                            snackbar = { msg, duration ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = msg,
                                        duration = duration
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

