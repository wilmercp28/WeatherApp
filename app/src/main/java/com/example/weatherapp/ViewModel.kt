package com.example.weatherapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ViewModel: ViewModel(){
    private var weather = "Cloudy"
    val calendar = Calendar.getInstance()
    @Composable
    fun WeatherIcon(): ImageBitmap {
        val sun = ImageBitmap.imageResource(R.drawable.sun)
        val cloudy = ImageBitmap.imageResource(R.drawable.cloudy)
        val raining = ImageBitmap.imageResource(R.drawable.raining)
        val defaultWeather = ImageBitmap.imageResource(R.drawable.defaultweather)
        when (weather) {
            "Sunny" -> return sun
            "Cloudy" -> return cloudy
            "Raining" -> return raining
            else -> return defaultWeather
        }

    }

    @Composable
    fun GetTime(): String {
        val timeFormat = SimpleDateFormat("hh:mm \na", Locale.getDefault())

        return timeFormat.format(calendar.time)
    }
    fun GetMonth(): String {
        val dateFormat = SimpleDateFormat("MM", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
    fun GetDay(): String {
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}
