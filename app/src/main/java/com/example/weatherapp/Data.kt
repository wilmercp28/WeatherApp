package com.example.weatherapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.gson.Gson


data class Data(
    val apiKey: String = "983609e5f914830a669a8dd853fd34cb",

)

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
@Composable
fun weatherData() {
    val data = Data()
    var parsedWeatherData: WeatherData? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        var weatherDataJson = WeatherAPI().getWeatherData(40.712776, -74.005974, data.apiKey)
        val gson = Gson()
        parsedWeatherData = gson.fromJson(weatherDataJson, WeatherData::class.java)
    }
    parsedWeatherData?.let {
        val lat = it.lat
        val lon = it.lon
        val timezone = it.timezone
        val currentWeather = it.current
    }
}
