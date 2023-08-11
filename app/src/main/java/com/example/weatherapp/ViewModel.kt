package com.example.weatherapp

import android.util.Log
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
import java.util.TimeZone
import kotlin.math.roundToInt

class ViewModel {
    var weatherData: MutableState<Data?> = mutableStateOf(null)
    var geoData: MutableState<GeoData?> = mutableStateOf(null)
    var lat: String? by mutableStateOf(null)
    var lon: String? by mutableStateOf(null)
    private val viewModelScope = CoroutineScope(Dispatchers.Main)
    private var currentTime: Date by mutableStateOf(Calendar.getInstance().time)
    private val locale: Locale = Locale.getDefault()
    val formattedTime: String = SimpleDateFormat("hh:mm a", locale).format(currentTime)
    val formattedDayAndMonth: String = SimpleDateFormat("MM dd", locale).format(currentTime)
    var currentTemperature: Int? by mutableStateOf(null)
    var unit:String by mutableStateOf("metric")
    var unitLetter by mutableStateOf("C")
    var currentWeatherIcon: String? by mutableStateOf(null)
    var dailyWeatherIcon: String? by mutableStateOf(null)
    var currentWeatherDescription: String? by mutableStateOf(null)
    var currentTemperatureFeelsLike: Int? by mutableStateOf(null)
    var cityName: String? by mutableStateOf(null)
    var forecastTimeDaily: String? by mutableStateOf(null)
    var morningTemperature: Int? by mutableStateOf(null)
    var dayTemperature: Int? by mutableStateOf(null)
    var eveningTemperature: Int? by mutableStateOf(null)
    var nightTemperature: Int? by mutableStateOf(null)
    var dailySummary: String? by mutableStateOf(null)
    var zipCode: String? by mutableStateOf(null)




     fun fetchWeatherData() {
         if (weatherData.value == null) {
             viewModelScope.launch {
                 if (lat == null && lon == null){
                     geoData.value = GeocodingAPI.getGeoData(zipCode)
                     lat = geoData.value?.lat.toString()
                     lon = geoData.value?.lon.toString()
                     Log.d("geoData", geoData.value.toString())
                 } else if (lat != null && lon != null && weatherData.value == null) {
                 try {
                     val lat = lat.toString() // Your latitude
                     val lon = lon.toString() // Your longitude
                     val currentUnit = unit
                     weatherData.value = WeatherAPI.getWeatherData(lat, lon, currentUnit)
                     formatData()
                     Log.d("1Temo", weatherData.value.toString())
                     Log.d("Unit ViewModel", currentUnit)
                 } catch (e: Exception) {
                     // Handle the error if needed
                 }
                 }
             }
         }
     }
    fun formatData(){
        viewModelScope.launch {
            currentTemperature = weatherData.value?.current?.temp?.roundToInt()
            morningTemperature = weatherData.value?.daily?.get(0)?.temp?.morn?.roundToInt()
            dayTemperature = weatherData.value?.daily?.get(0)?.temp?.day?.roundToInt()
            eveningTemperature = weatherData.value?.daily?.get(0)?.temp?.eve?.roundToInt()
            nightTemperature = weatherData.value?.daily?.get(0)?.temp?.night?.roundToInt()
            val currentWeatherIconCode = weatherData.value?.current?.weather?.get(0)?.icon
            val dailyWeatherIconCode = weatherData.value?.daily?.get(0)?.weather?.get(0)?.icon
            val hourlyWeatherIconCode = weatherData.value?.hourly?.get(0)?.weather?.get(0)?.icon
            currentWeatherIcon = "https://openweathermap.org/img/wn/$currentWeatherIconCode@2x.png"
            dailyWeatherIcon = "https://openweathermap.org/img/wn/$dailyWeatherIconCode@2x.png"
            currentWeatherDescription = weatherData.value?.current?.weather?.get(0)?.description
            currentTemperatureFeelsLike = weatherData.value?.current?.feels_like?.roundToInt()
            forecastTimeDaily = convertUnixTimeToLocalTime(weatherData.value?.daily?.get(0)?.dt!!)
            dailySummary = weatherData.value?.daily?.get(0)?.summary
            cityName = geoData.value?.name
        }
    }
    fun convertUnixTimeToLocalTime(unixTime: Long): String {
        val utcTimeInMillis = unixTime * 1000 // Convert to milliseconds
        val timeZone = TimeZone.getDefault() // Get the default time zone
        val localTimeInMillis = utcTimeInMillis + timeZone.rawOffset // Apply time zone offset
        val localDate = Date(localTimeInMillis)
        val dateFormat = SimpleDateFormat("HH:mm a", Locale.getDefault()) // Adjust the format as needed
        return dateFormat.format(localDate)
    }
    fun updateZipCode(newZipCode: String) {
        zipCode = newZipCode
    }
 }









