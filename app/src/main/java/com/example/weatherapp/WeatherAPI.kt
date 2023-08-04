package com.example.weatherapp

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException

class WeatherAPI(private val apiKey: String) {
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()
    private var temperature: Double = 0.0


    fun getWeatherData(
        latitude: Double,
        longitude: Double,
        exclude: String? = null,
        units: String? = null,
        lang: String? = null,
        callback: (WeatherData?) -> Unit

    ){
        val url = "https://api.openweathermap.org/data/3.0/onecall" +
                "?lat=$latitude&lon=$longitude&appid=$apiKey" +
                if (exclude != null) "&exclude=$exclude" else "" +
                        if (units != null) "&units=$units" else "" +
                                if (lang != null) "&lang=$lang" else ""

        val request = Request.Builder()
            .url(url)
            .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                val weatherData = parseWeatherData(responseData)
                temperature = weatherData?.temperature ?: 0.0
                callback(weatherData)
            }
        })
    }
    private fun parseWeatherData(responseData: String?): WeatherData? {
        // Implement JSON parsing logic here to parse the API response
        // and return the WeatherData object
        return null
    }
    fun getTemperature(): Double {
        return temperature
    }
}
data class WeatherData(
val temperature: Double,
)


