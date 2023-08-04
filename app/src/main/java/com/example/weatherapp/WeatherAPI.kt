package com.example.weatherapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

class WeatherAPI() {
    private val client = HttpClient()


    suspend fun getWeatherData(lat: Double, lon: Double, appid: String): String {
        val httpResponse: HttpResponse =
            client.get("https://api.openweathermap.org/data/3.0/onecall?lat=$lat&lon=$lon&appid=$appid")
        return httpResponse.body<String>().toString()
    }

}





