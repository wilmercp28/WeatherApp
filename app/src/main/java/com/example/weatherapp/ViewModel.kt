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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

class ViewModel{
    var weatherData: MutableState<Data?> = mutableStateOf(null)
    var geoData: MutableState<GeoData?> = mutableStateOf(null)
    var foreCastData: MutableState<ForecastData?> = mutableStateOf(null)
    var lat: String? by mutableStateOf(null)
    var lon: String? by mutableStateOf(null)
    private val viewModelScope = CoroutineScope(Dispatchers.Main)
    var currentTime: Date by mutableStateOf(Calendar.getInstance().time)
    private val locale: Locale = Locale.getDefault()
    var currentTemperature: Int? by mutableStateOf(null)
    var unit:String by mutableStateOf("metric")
    var unitLetter by mutableStateOf("C")
    var currentWeatherIcon: String? by mutableStateOf(null)
    var dailyWeatherIcon: String? by mutableStateOf(null)
    var currentWeatherDescription: String? by mutableStateOf(null)
    var currentTemperatureFeelsLike: Int? by mutableStateOf(null)
    var cityName: String? by mutableStateOf(null)
    var morningTemperature: Int? by mutableStateOf(null)
    var dayTemperature: Int? by mutableStateOf(null)
    var eveningTemperature: Int? by mutableStateOf(null)
    var nightTemperature: Int? by mutableStateOf(null)
    var dailySummary: String? by mutableStateOf(null)
    var zipCode: String? by mutableStateOf(null)
    var isCountingDown: Boolean by mutableStateOf(false)
    var whenToRefresh: Date by mutableStateOf(currentTime)
    var isValidZipCode: Boolean by mutableStateOf(true)
    var temperatureList: MutableList<Double> = mutableListOf()



    fun fetchWeatherData() {
        if (weatherData.value == null) {
            viewModelScope.launch {
                val currentUnit = unit
                weatherData.value = WeatherAPI.getWeatherData(lat.toString(), lon.toString(), currentUnit)
                foreCastData.value = ForecastAPI.getForecastData(lat.toString(),lon.toString(),currentUnit)
                if (weatherData.value != null && foreCastData.value != null) {
                    formatData()
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
            Log.d("ZipCode","ZipCode is $zipCode")
            if (zipCode != null) {
                geoData.value = GeocodingAPI.getGeoData(zipCode)
                Log.d("GeoData",geoData.value.toString())
                if (geoData.value?.zip == null){
                    Log.d("ZipCodeGeo",geoData.value?.zip.toString())
                    isValidZipCode = false
                   zipCode = ""
                } else {
                    lat = geoData.value?.lat
                    lon = geoData.value?.lon
                    SaveData.saveData(context, "zipcode", zipCode)
                    Log.d("SaveData","Zip Code Saved, now is $zipCode")
                    if (lat != null && lon != null) {
                        SaveData.saveData(context,"zipCode",zipCode)
                        isValidZipCode = true
                        fetchWeatherData()
                    }
                }
            }
        }
    }

    fun formatData(){
        Log.d("Format","Data Formatted")
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
            dailySummary = weatherData.value?.daily?.get(0)?.summary
            cityName = geoData.value?.name
        }
    }
    fun convertUnixTimeToLocalTime(unixTime: Long, dateFormat: String): String {
        val utcTimeInMillis = unixTime * 1000 // Convert to milliseconds
        val timeZone = TimeZone.getDefault() // Get the default time zone
        val localTimeInMillis = utcTimeInMillis + timeZone.rawOffset // Apply time zone offset
        val localDate = Date(localTimeInMillis)
        val dateFormat = SimpleDateFormat(dateFormat ,Locale.getDefault()) // Adjust the format as needed
        return dateFormat.format(localDate)
    }
    fun init(context: Context) {
        Log.d("Init","Init Run")
        if (SaveData.getData(context,"zipCode") == null) {
            zipCode = ""
        } else{
            zipCode = SaveData.getData(context, "zipCode")
        }
        if (SaveData.getData(context,"unit") == null){
            unit = "imperial"
            unitLetter = "F"
        } else {
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
    fun stringToDate(dateString: String, format: String): Date? {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.parse(dateString)
    }

    fun getCurrentTime(format: String): String {
        val currentTime  = Calendar.getInstance().time
        val formatDate = SimpleDateFormat(format, locale)
        return formatDate.format(currentTime)
    }
    fun getWeatherIcon(iconCode: String): String {
        return "https://openweathermap.org/img/wn/$iconCode@2x.png"
    }
}










