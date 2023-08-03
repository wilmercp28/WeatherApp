package com.example.weatherapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
open class ViewModel {
    private val viewModelScope = CoroutineScope(Dispatchers.Main)
    private var weather = "Cloudy"
    private var currentTime: Date by mutableStateOf(Calendar.getInstance().time)
    private val locale: Locale = Locale.getDefault()
    val formattedTime: String = SimpleDateFormat("hh:mm", locale).format(currentTime)
    val formattedAmPm: String = SimpleDateFormat("a", locale).format(currentTime)
    val formattedDay: String = SimpleDateFormat("dd", locale).format(currentTime)
    val formattedMonth: String = SimpleDateFormat("MM",locale).format(currentTime)
    val _weatherData: MutableState<WeatherData?> = mutableStateOf(null)
    private val apiKey = "49338c40fc6197e95db0757dfc52177f"
    private val weatherApi = WeatherAPI(apiKey)
    val weatherData = mutableStateOf<WeatherData?>(null)


    fun fetchWeatherData(latitude: Double, longitude: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            GlobalScope.launch(Dispatchers.IO) {
                weatherApi.getWeatherData(latitude, longitude) { data ->
                    weatherData.value = data
                }
            }
        }
    }

    init {
        updateTimePeriodically()
    }
    private fun updateTimePeriodically() {
        viewModelScope.launch {
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




