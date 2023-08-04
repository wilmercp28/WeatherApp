package com.example.weatherapp

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.call.receive
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.request.HttpRequest
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Url
import io.ktor.http.isSuccess

class WeatherAPI(private val apiKey: String) {
    private val client = HttpClient(CIO)
    data class WeatherData(
        val lat: Double,
        val lon: Double,
        val timezone: String,
        val timezone_offset: Int,
        val current: CurrentWeather,

    )

    data class TemperatureInfo(
        val day: Double,
        val min: Double,
        val max: Double,
        val night: Double,
        val eve: Double,
        val morn: Double
    )
    data class CurrentWeather(
        val dt: Long,
        val sunrise: Long,
        val sunset: Long,
        val temp: Double,
        val feels_like: Double,
        val pressure: Int,
        val humidity: Int,
        val dew_point: Double,
        val uvi: Double,
        val clouds: Int,
        val visibility: Int,
        val wind_speed: Double,
        val wind_deg: Int,
        val wind_gust: Double,
        val weather: List<WeatherInfo>
    )
    data class WeatherInfo(
        val id: Int,
        val main: String,
        val description: String,
        val icon: String
    )

    suspend fun getWeatherData(lat: Double, lon: Double, exclude: String): HttpResponse? {
        val url = "https://api.openweathermap.org/data/3.0/onecall"
        val response: HttpResponse = client.request("$url"){
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("exclude", exclude)
            parameter("appid", apiKey)
        }
        if (response != null) {
            return response
        }
            return null
        }
    }




