package com.example.weatherapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale
open class ViewModel {
    private var weather = "Cloudy"
    var currentTime by mutableStateOf(Calendar.getInstance().time)
    init {
        updateTimePeriodically()
    }
    private fun updateTimePeriodically() {
        GlobalScope.launch {
            while (true) {
                delay(1000) // Update every 1 second
                currentTime = Calendar.getInstance().time
            }
        }
    }

    @Composable
    fun weatherIcon(): ImageBitmap {
        val sun = ImageBitmap.imageResource(R.drawable.sun)
        val cloudy = ImageBitmap.imageResource(R.drawable.cloudy)
        val raining = ImageBitmap.imageResource(R.drawable.raining)
        val defaultWeather = ImageBitmap.imageResource(R.drawable.defaultweather)
        return when (weather) {
            "Sunny" -> sun
            "Cloudy" -> cloudy
            "Raining" -> raining
            else -> defaultWeather
        }
    }
}




