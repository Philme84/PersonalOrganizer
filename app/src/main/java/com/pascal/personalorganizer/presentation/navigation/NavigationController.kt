package com.pascal.personalorganizer.presentation.navigation

import android.net.Uri
import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pascal.personalorganizer.domain.models.Notes
import com.pascal.personalorganizer.domain.models.Reminder
import com.pascal.personalorganizer.domain.models.Schedule
import com.pascal.personalorganizer.presentation.ui.MainViewModel
import com.pascal.personalorganizer.presentation.ui.notes.NotesScreen
import com.pascal.personalorganizer.presentation.ui.notes.add.AddNoteScreen
import com.pascal.personalorganizer.presentation.ui.reminders.RemindersScreen
import com.pascal.personalorganizer.presentation.ui.reminders.add.AddReminderScreen
import com.pascal.personalorganizer.presentation.ui.schedule.ScheduleScreen
import com.pascal.personalorganizer.presentation.ui.schedule.add.AddScheduleScreen
import com.pascal.personalorganizer.presentation.ui.weather.WeatherScreen

@Composable
fun NavigationController(
    navController: NavController,
    viewModel: MainViewModel,
    snackbar: (String, SnackbarDuration) -> Unit,
){
    NavHost(
        navController = navController as NavHostController,
        startDestination = Screens.NotesScreen.route
    ){

        composable(route = Screens.NotesScreen.route){
            NotesScreen(
                mainViewModel = viewModel,
                onNavigate = {
                    navController.navigate(it.route) {
                        launchSingleTop = true
                    }
                },
                onSnackBar = {
                    snackbar(it.msg, it.duration)
                }
            )
        }

        composable(
            route = Screens.AddNotesScreen.route + "/{id}?title={title}&description={description}&date={date}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                },
                navArgument("title") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("description") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("date") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ){ entry ->
            AddNoteScreen(
                mainViewModel = viewModel,
                notes = Notes(
                    id = entry.arguments?.getInt("id")!!,
                    title = entry.arguments?.getString("title")!!,
                    description = entry.arguments?.getString("description")!!,
                    date = entry.arguments?.getLong("date")!!,
                    uri = viewModel.uris
                ),
                onBack = {
                    navController.popBackStack()
                },
                onSnackBar = {
                    snackbar(it.msg, it.duration)
                }
            )
        }


        composable(route = Screens.ScheduleScreen.route){
            ScheduleScreen(
                mainViewModel = viewModel,
                onNavigate = {
                    navController.navigate(it.route) {
                        launchSingleTop = true
                    }
                },
                onSnackBar = {
                    snackbar(it.msg, it.duration)
                }
            )
        }
        composable(
            route = Screens.AddScheduleScreen.route + "/{id}?title={title}&selectedDay={selectedDay}&date={date}&reminder={reminder}&year={year}&month={month}&day={day}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                },
                navArgument("title") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("selectedDay") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("date") {
                    type = NavType.LongType
                    defaultValue = 0L
                },
                navArgument("reminder") {
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument("year") {
                    type = NavType.IntType
                },
                navArgument("month") {
                    type = NavType.IntType
                },
                navArgument("day") {
                    type = NavType.IntType
                },
            )
        ){ entry ->
            AddScheduleScreen(
                mainViewModel = viewModel,
                schedule = Schedule(
                    id = entry.arguments?.getInt("id")!!,
                    title = entry.arguments?.getString("title")!!,
                    selectedDay = entry.arguments?.getString("selectedDay")!!,
                    date = entry.arguments?.getLong("date")!!,
                    reminder = entry.arguments?.getBoolean("reminder")!!
                ),
                year = entry.arguments?.getInt("year")!!,
                month = entry.arguments?.getInt("month")!!,
                day = entry.arguments?.getInt("day")!!,
                onBack = {
                    navController.popBackStack()
                },
                onSnackBar = {
                    snackbar(it.msg, it.duration)
                }
            )
        }
        composable(route = Screens.ReminderScreen.route){
            RemindersScreen(
                mainViewModel = viewModel,
                onNavigate = {
                    navController.navigate(it.route) {
                        launchSingleTop = true
                    }
                },
                onSnackBar = {
                    snackbar(it.msg, it.duration)
                }
            )
        }
        composable(
            route = Screens.AddReminderScreen.route + "/{id}?title={title}&date={date}&repeatDaily={repeatDaily}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                },
                navArgument("title") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("date") {
                    type = NavType.LongType
                    defaultValue = 0L
                },
                navArgument("repeatDaily") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ){ entry ->
            AddReminderScreen(
                mainViewModel = viewModel,
                reminder = Reminder(
                    id = entry.arguments?.getInt("id")!!,
                    title = entry.arguments?.getString("title")!!,
                    date = entry.arguments?.getLong("date")!!,
                    repeatDaily = entry.arguments?.getBoolean("repeatDaily")!!
                ),
                onBack = {
                    navController.popBackStack()
                },
                onSnackBar = {
                    snackbar(it.msg, it.duration)
                }
            )
        }
        composable(route = Screens.WeatherScreen.route){
            WeatherScreen(mainViewModel = viewModel)
        }

    }
}