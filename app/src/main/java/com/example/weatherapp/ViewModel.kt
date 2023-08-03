package com.example.weatherapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ViewModel: ViewModel(){
    private var weather = "Cloudy"
    private val calendar: Calendar = Calendar.getInstance()
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

    @Composable
    fun getTime(): String {
        val timeFormat = remember { SimpleDateFormat("hh:mm \na", Locale.getDefault()) }

        return timeFormat.format(calendar.time)
    }
    fun getMonth(): String {
        val dateFormat = SimpleDateFormat("MM", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
    fun getDay(): String {
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}
