package com.example.weatherapp

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import io.ktor.client.call.body
import io.ktor.client.call.receive
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
 class ViewModel {
     var weatherData: MutableState<Data?> = mutableStateOf(null)
     private val viewModelScope = CoroutineScope(Dispatchers.Main)
     private var weather = "Cloudy"
     private var currentTime: Date by mutableStateOf(Calendar.getInstance().time)
     private val locale: Locale = Locale.getDefault()
     val formattedTime: String = SimpleDateFormat("hh:mm", locale).format(currentTime)
     val formattedAmPm: String = SimpleDateFormat("a", locale).format(currentTime)
     val formattedDay: String = SimpleDateFormat("dd", locale).format(currentTime)
     val formattedMonth: String = SimpleDateFormat("MM", locale).format(currentTime)
     fun fetchWeatherData() {
         if (weatherData.value == null) { // Check if data is already available
             viewModelScope.launch {
                 try {
                     val lat = 40.712776 // Your latitude
                     val lon = -74.005974 // Your longitude
                     val apiKey = "983609e5f914830a669a8dd853fd34cb" // Your API key
                     val weatherAPI = WeatherAPI
                     weatherData.value = weatherAPI.getWeatherData(lat, lon, apiKey)
                     Log.d("1Temo", weatherData.value.toString())

                 } catch (e: Exception) {
                     // Handle the error if needed
                 }
             }
         }
     }
 }
@Composable
fun WeatherIcon(weatherIconName: String): ImageBitmap {
    val sun = ImageBitmap.imageResource(R.drawable.sun)
    val cloudy = ImageBitmap.imageResource(R.drawable.cloudy)
    val raining = ImageBitmap.imageResource(R.drawable.raining)
    return when (weatherIconName) {
        "Sunny" -> sun
        "Cloudy" -> cloudy
        "Raining" -> raining
        else -> sun
    }
}







