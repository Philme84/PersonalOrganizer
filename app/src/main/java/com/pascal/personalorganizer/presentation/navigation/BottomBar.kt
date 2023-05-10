package com.pascal.personalorganizer.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.pascal.personalorganizer.ui.theme.grey
import com.pascal.personalorganizer.ui.theme.softBlack

@Composable
fun BottomBar(
    screen: Screens,
    navHostController: NavHostController
){
    var showBottomBar by remember { mutableStateOf(true) }
    showBottomBar = screen.title != Screens.AddReminderScreen.title && screen.title != Screens.AddScheduleScreen.title && screen.title != Screens.AddNotesScreen.title

    val currentRoute = screen.route
    val screenObjects = listOf<Screens>(
        Screens.NotesScreen,
        Screens.ScheduleScreen,
        Screens.ReminderScreen,
        Screens.WeatherScreen
    )

    //Don't show the bottom bar if we are in any of the Add screens, we are using enter and exit animations.
    AnimatedVisibility(visible = showBottomBar,enter = fadeIn(tween(500)) + expandVertically(
        animationSpec = tween(
            500,
            easing = EaseIn
        )
    ), exit = fadeOut(tween(500)) + shrinkVertically(
        animationSpec = tween(
            500,
            easing = EaseOut
        )
    )) {
        BottomNavigation(backgroundColor = softBlack) {
            screenObjects.forEach { screen ->
                AddItem(
                    screen = screen,
                    currentRoute = currentRoute,
                    navController = navHostController
                )
            }
        }
    }

}

@Composable
fun RowScope.AddItem(
    screen: Screens,
    currentRoute: String,
    navController: NavHostController
) {
    BottomNavigationItem(
        label = { Text(text = screen.title, style = MaterialTheme.typography.h4) },
        icon = { Icon(painter = painterResource(id = screen.icon), contentDescription = null)},
        selected = screen.route == currentRoute,
        selectedContentColor = MaterialTheme.colors.onSurface,
        unselectedContentColor = grey,
        onClick = {
            navController.popBackStack()
            navController.navigate(screen.route) {
                launchSingleTop = true
            }
        }
    )
}