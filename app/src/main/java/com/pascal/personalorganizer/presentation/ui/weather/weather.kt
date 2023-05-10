package com.pascal.personalorganizer.presentation.ui.weather

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.location.LocationServices
import com.pascal.personalorganizer.data.local.entities.DailyForecast
import com.pascal.personalorganizer.data.local.entities.TodayForecast
import com.pascal.personalorganizer.presentation.composables.*
import com.pascal.personalorganizer.presentation.navigation.Screens
import com.pascal.personalorganizer.presentation.ui.MainViewModel
import com.pascal.personalorganizer.ui.theme.backgroundGrey
import java.text.DateFormat
import java.util.*
import android.provider.Settings
import android.util.Log
import androidx.compose.material.*
import androidx.compose.ui.graphics.Color

private val permissionsToRequest = arrayOf(
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION,
)
@Composable
fun WeatherScreen(mainViewModel: MainViewModel, weatherViewModel: WeatherViewModel = hiltViewModel()){

    LaunchedEffect(Unit){
        mainViewModel.setCurrentScreen(Screens.WeatherScreen)
    }

    val context = LocalContext.current
    val activity = context as Activity
    val weather = weatherViewModel.forecast
    val dialogQueue = weatherViewModel.visiblePermissionDialogQueue
    val isReady = weatherViewModel.isReady
    val dayList = weatherViewModel.dayList

    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            permissionsToRequest.forEach { permission ->
                weatherViewModel.onPermissionResult(
                    permission = permission,
                    isGranted = perms[permission] == true
                )
                if (perms[permission] == true){
                    getLocation(context, weatherViewModel)
                }
            }
        }
    )

    LaunchedEffect(Unit){
        multiplePermissionResultLauncher.launch(permissionsToRequest)
        getLocation(context, weatherViewModel)
    }

    dialogQueue
        .reversed()
        .forEach { permission ->
            PermissionDialog(
                permissionTextProvider = when (permission) {
                    Manifest.permission.ACCESS_FINE_LOCATION -> {
                        FineLocationTextProvider()
                    }
                    Manifest.permission.ACCESS_COARSE_LOCATION -> {
                        CoarseLocationTextProvider()
                    }
                    else -> return@forEach
                },
                isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                    activity,
                    permission
                ),
                onDismiss = weatherViewModel::dismissDialog,
                onOkClick = {
                    weatherViewModel.dismissDialog()
                    multiplePermissionResultLauncher.launch(
                        arrayOf(permission)
                    )
                },
                onGoToAppSettingsClick = { activity.openAppSettings() }
            )
        }

    Crossfade(targetState = isReady) {state ->
        when(state){
            "loading" -> {
                Shimmer {
                    WeatherShimmer(brush = it)
                }
            }
            "GPSOff" -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Turn GPS On", style = TextStyle(color = Color.Red))
                    Button(onClick = {

                        getLocation(context, weatherViewModel)
                    }) {
                        Text(text = "Retry")
                    }
                }
            }
            "ready" ->{
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colors.surface)
                            .padding(horizontal = 50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),text = weather.name, textAlign = TextAlign.Center, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, color = MaterialTheme.colors.onSurface))
                            AsyncImage(
                                modifier = Modifier
                                    .size(128.dp),
                                model = ImageRequest.Builder(context)
                                    .data(weather.currentConditionIconUrl)
                                    .crossfade(true)
                                    .build(),
                                contentScale = ContentScale.Crop,
                                contentDescription = null
                            )
                            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.Center) {
                                Text(modifier = Modifier, text = weather.currentTempF, style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 75.sp), color = MaterialTheme.colors.onBackground)
                                Text(modifier = Modifier.offset(y = 15.dp), text = " °F", style = MaterialTheme.typography.caption, color = MaterialTheme.colors.onBackground)
                            }
                            Text(modifier = Modifier
                                .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                                .fillMaxWidth(), text = weather.currentCondition, textAlign = TextAlign.Center, style = MaterialTheme.typography.h6, color = MaterialTheme.colors.onBackground)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colors.surface)
                            .padding(8.dp)
                            .wrapContentWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = backgroundGrey )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "24 hour forecast", style = MaterialTheme.typography.h5)
                            }
                            LazyRow(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                items(weather.todayForecast){
                                    ForecastCell(it)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colors.surface)
                            .padding(8.dp)
                            .wrapContentWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = backgroundGrey
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "3 days forecast", style = MaterialTheme.typography.h5)
                            }
                            if (weather.dailyForecast.isNotEmpty()){
                                for (index in weather.dailyForecast.indices){
                                    ForecastCell(day = dayList[index], forecast = weather.dailyForecast[index])
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}


private fun getLocation(context: Context, viewModel: WeatherViewModel) {

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    try {
        val location = fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null){
                viewModel.getForecast("${it.latitude},${it.longitude}")
            }
        }
    } catch (e: SecurityException) {
       Log.d("WEATHER", e.message.toString())
    } catch (e: Exception) {
        Log.d("WEATHER", e.message.toString())
    }
}

@Composable
fun ForecastCell(forecast: TodayForecast){
    val context = LocalContext.current
    val dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())

    Column(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.offset(x = 5.dp) ,verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.Center) {
            Text(modifier = Modifier, text = forecast.tempF, style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 30.sp), color = MaterialTheme.colors.onBackground)
            Text(modifier = Modifier.offset(y = 8.dp), text = " °F", style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp), color = MaterialTheme.colors.onBackground)
        }
        AsyncImage(
            modifier = Modifier
                .size(25.dp),
            model = ImageRequest.Builder(context)
                .data(forecast.iconUrl)
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        Text(text = dateFormat.format(Date(forecast.epoch*1000L)), style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, color = MaterialTheme.colors.onSurface))
    }
}

@Composable
fun ForecastCell(day: String, forecast: DailyForecast){
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                AsyncImage(
                    modifier = Modifier
                        .size(35.dp),
                    model = ImageRequest.Builder(context)
                        .data(forecast.iconUrl)
                        .crossfade(true)
                        .build(),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "$day  ${forecast.condition}", style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, color = MaterialTheme.colors.onSurface ))
            }
            Row{
                Text(text = "${forecast.minTempF} / ${forecast.maxTempF}", style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, color = MaterialTheme.colors.onSurface ))
            }
        }
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}