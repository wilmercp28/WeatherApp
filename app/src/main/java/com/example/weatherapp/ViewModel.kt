package com.example.weatherapp

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

class ViewModel{
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
    var zipCode: String? by mutableStateOf("")
    var isCountingDown: Boolean by mutableStateOf(false)
    var whenToRefresh: Date by mutableStateOf(currentTime)
     fun fetchWeatherData() {
         Log.d("ZipCode", zipCode.toString())
         if (weatherData.value == null) {
             viewModelScope.launch {
                 try {
                     val currentUnit = unit
                     weatherData.value = WeatherAPI.getWeatherData(lat.toString(), lon.toString(), currentUnit)
                     formatData()
                     Log.d("Unit ViewModel", currentUnit)

                 } catch (e: Exception) {

                 }
             }
         }
     }
    fun fetchGeoData(context: Context) {
        if (zipCode != geoData.value?.zip){
            WeatherAPI.clearCache()
            GeocodingAPI.clearCache()
            weatherData.value = null
            geoData.value = null
        }
        viewModelScope.launch {
            if (zipCode != null) {
                geoData.value = GeocodingAPI.getGeoData(zipCode)
                lat = geoData.value?.lat
                lon = geoData.value?.lon
                Log.d("geoData", geoData.value.toString())
                SaveData.saveData(context,"zipcode",zipCode)
                if (weatherData.value != null) {
                    fetchWeatherData()
                }
            }
        }
    }

    fun formatData(){
        Log.d("FOrmat","Data Formatted")
        viewModelScope.launch {
            currentTemperature = weatherData.value?.current?.temp?.roundToInt()
            morningTemperature = weatherData.value?.daily?.get(0)?.temp?.morn?.roundToInt()
            dayTemperature = weatherData.value?.daily?.get(0)?.temp?.day?.roundToInt()
            eveningTemperature = weatherData.value?.daily?.get(0)?.temp?.eve?.roundToInt()
            nightTemperature = weatherData.value?.daily?.get(0)?.temp?.night?.roundToInt()
            val currentWeatherIconCode = weatherData.value?.current?.weather?.get(0)?.icon
            val dailyWeatherIconCode = weatherData.value?.daily?.get(0)?.weather?.get(0)?.icon
            currentWeatherIcon = "https://openweathermap.org/img/wn/$currentWeatherIconCode@2x.png"
            dailyWeatherIcon = "https://openweathermap.org/img/wn/$dailyWeatherIconCode@2x.png"
            currentWeatherDescription = weatherData.value?.current?.weather?.get(0)?.description
            currentTemperatureFeelsLike = weatherData.value?.current?.feels_like?.roundToInt()
            forecastTimeDaily = convertUnixTimeToLocalTime(weatherData.value?.daily?.get(0)?.dt!!)
            dailySummary = weatherData.value?.daily?.get(0)?.summary
            cityName = geoData.value?.name
        }
    }
    private fun convertUnixTimeToLocalTime(unixTime: Long): String {
        val utcTimeInMillis = unixTime * 1000 // Convert to milliseconds
        val timeZone = TimeZone.getDefault() // Get the default time zone
        val localTimeInMillis = utcTimeInMillis + timeZone.rawOffset // Apply time zone offset
        val localDate = Date(localTimeInMillis)
        val dateFormat = SimpleDateFormat("HH:mm a", Locale.getDefault()) // Adjust the format as needed
        return dateFormat.format(localDate)
    }
    fun init(context: Context) {
        WeatherAPI.clearCache()
        if (SaveData.getData(context,"zipCode") == null){
            zipCode = ""
            unit = "imperial"
            unitLetter = "F"
        } else {
            zipCode = SaveData.getData(context, "zipCode")
            unit = SaveData.getData(context, "unit").toString()
            unitLetter = SaveData.getData(context, "unitLetter").toString()
        }
        refresh()
    }
    object SaveData {
        private const val PREFS_NAME = "app_prefs"

        fun saveData(context: Context, key: String, value: String?) {
            val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(key, value).apply()
        }

        fun getData(context: Context, key: String): String? {
            val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString(key, null)
        }
    }
    fun refresh() {
        viewModelScope.launch {
            while (true){
                val currentTime = Calendar.getInstance().time
                if (!isCountingDown) {
                    whenToRefresh = addMinutesToDate(currentTime,10)
                    isCountingDown = true
                } else if (currentTime >= whenToRefresh){
                    weatherData.value = null
                    WeatherAPI.clearCache()
                    isCountingDown = false
                }
                delay(60000)
            }
        }
    }
        fun addMinutesToDate(date: Date, minutesToAdd: Int): Date {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.MINUTE, minutesToAdd)
            return calendar.time
        }
    }










