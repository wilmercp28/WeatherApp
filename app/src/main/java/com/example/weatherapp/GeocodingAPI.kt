package com.example.weatherapp

import android.util.Log
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

object GeocodingAPI{
    private val client = HttpClient()
    private var cachedGeoData: Data? = null
    suspend fun getWeatherData(lat: Double, lon: Double, appid: String, unit: String): Data? {
        if (cachedGeoData != null) {
            Log.d("GeoAPI Cache", "Raw JSON response: $cachedGeoData")
            return cachedGeoData
        } else {
            val httpResponse: HttpResponse =
                client.get("")
            val geoDataJson = httpResponse.body<String>()
            Log.d("GeoAPI", "Raw JSON response: $geoDataJson")
            val gson = Gson()
            val geoData = gson.fromJson(geoDataJson, Data::class.java)
            cachedGeoData = geoData
            return cachedGeoData
        }
    }

}