package com.example.weatherapp

import android.util.Log
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

object ForecastAPI {
    private val client = HttpClient()
    private var cachedForecastData: ForecastData? = null
    val apiKey = "983609e5f914830a669a8dd853fd34cb"
    suspend fun getForecastData(lat: String, lon: String,unit: String): ForecastData? {
        if (cachedForecastData != null) {
            Log.d("Forecast API Cache", "Raw JSON response: $cachedForecastData")
            return cachedForecastData
        } else {
            val httpResponse: HttpResponse =
                client.get("https://api.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$lon&appid=$apiKey&units=$unit&cnt=40")
            val forecastDataJson = httpResponse.body<String>()
            Log.d("ForeCast API", "Raw JSON response: $forecastDataJson")
            val gson = Gson()
            val forecastData = gson.fromJson(forecastDataJson, ForecastData::class.java)
            cachedForecastData = forecastData
            return cachedForecastData
        }
    }
    fun clearCache() {
        cachedForecastData = null
    }

}
