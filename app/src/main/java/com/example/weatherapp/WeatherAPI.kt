package com.example.weatherapp

import android.util.Log
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse


object WeatherAPI{
    private val client = HttpClient()
    private var cachedWeatherData: Data? = null
    val apiKey = "983609e5f914830a669a8dd853fd34cb"
    suspend fun getWeatherData(lat: String, lon: String, unit: String): Data? {
        if (cachedWeatherData != null) {
            Log.d("WeatherAPI Cache", "Raw JSON response: $cachedWeatherData")
            return cachedWeatherData
        } else {
            val httpResponse: HttpResponse =
                client.get("https://api.openweathermap.org/data/3.0/onecall?lat=$lat&lon=$lon&units=$unit&appid=$apiKey")
            val weatherDataJson = httpResponse.body<String>()
            Log.d("WeatherAPI", "Raw JSON response: $weatherDataJson")
            val gson = Gson()
            val weatherData = gson.fromJson(weatherDataJson, Data::class.java)
            cachedWeatherData = weatherData
            return cachedWeatherData
        }
    }
    fun clearCache() {
        cachedWeatherData = null
    }
}





