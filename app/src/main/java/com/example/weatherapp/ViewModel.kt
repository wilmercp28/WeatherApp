package com.example.weatherapp

import android.util.Log
import androidx.compose.material3.Switch
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class ViewModel {
    var weatherData: MutableState<Data?> = mutableStateOf(null)
    private val viewModelScope = CoroutineScope(Dispatchers.Main)
    private var currentTime: Date by mutableStateOf(Calendar.getInstance().time)
    private val locale: Locale = Locale.getDefault()
    val formattedTime: String = SimpleDateFormat("hh:mm a", locale).format(currentTime)
    val formattedDayAndMonth: String = SimpleDateFormat("MM dd", locale).format(currentTime)
    var currentTemperature: Int? by mutableStateOf(null)
    var unit = "imperial"
    var unitLetter = when(unit){
        "imperial" -> "F"
        "metric" -> "C"
        else -> {"Invalid"}
    }
    var currentWeatherIcon: String? by mutableStateOf(null)
    var currentWeatherDescription: String? by mutableStateOf(null)
    var currentTemperatureFeelsLike: Int? by mutableStateOf(null)
     fun fetchWeatherData() {
         if (weatherData.value == null) { // Check if data is already available
             viewModelScope.launch {
                 try {
                     val lat = 40.712776 // Your latitude
                     val lon = -74.005974 // Your longitude
                     val apiKey = "983609e5f914830a669a8dd853fd34cb" // Your API key
                     val weatherAPI = WeatherAPI
                     weatherData.value = weatherAPI.getWeatherData(lat, lon, apiKey, unit)
                     formatData()
                     Log.d("1Temo", weatherData.value.toString())
                 } catch (e: Exception) {
                     // Handle the error if needed
                 }
             }
         }
     }
    fun formatData(){
        viewModelScope.launch {
            currentTemperature = weatherData.value?.current?.temp?.roundToInt()
            val currentWeatherIconCode = weatherData.value?.current?.weather?.get(0)?.icon
            currentWeatherIcon = "https://openweathermap.org/img/wn/$currentWeatherIconCode@2x.png"
            currentWeatherDescription = weatherData.value?.current?.weather?.get(0)?.description
            currentTemperatureFeelsLike = weatherData.value?.current?.feels_like?.roundToInt()
        }
    }
 }








